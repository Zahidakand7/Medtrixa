#!/usr/bin/env python3
"""Second-pass retry for rows still no_data after refetch_fast.py.

Adds two fallbacks the first pass didn't try:
  1. Strip common dosage-form suffixes (XR, ER, CR, SR, DR, LA, XL, ODT)
     before searching, since openFDA's generic_name often omits them.
  2. Drop the HUMAN PRESCRIPTION DRUG filter as a last resort, so
     discontinued/recalled or OTC-labeled brands can still be found.

Usage:
  python3 refetch_pass2.py INPUT.csv OUTPUT.csv [--limit N] [--workers 8]

INPUT.csv should be the output of refetch_fast.py (has a status column).
Requires: export OPENFDA_API_KEY=...
Progress cached in refetch_cache_pass2.jsonl (separate from pass 1's cache).
Standard library only.
"""
import csv, json, os, re, sys, time, threading
import urllib.error, urllib.parse, urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed

API = "https://api.fda.gov/drug/label.json"
KEY = os.environ.get("OPENFDA_API_KEY", "")
CACHE = "refetch_cache_pass2.jsonl"
RX_FILTER = ' AND openfda.product_type:"HUMAN PRESCRIPTION DRUG"'

SUFFIXES = ["XR", "ER", "CR", "SR", "DR", "LA", "XL", "ODT", "CD", "SA"]
SUFFIX_RE = re.compile(
    r"\s+(?:" + "|".join(SUFFIXES) + r")\b\.?$", re.I
)

MAX_PER_SEC = 3.5
_lock = threading.Lock()
_next_slot = [0.0]


def throttle():
    with _lock:
        now = time.time()
        wait = max(0.0, _next_slot[0] - now)
        _next_slot[0] = max(now, _next_slot[0]) + (1.0 / MAX_PER_SEC)
    if wait:
        time.sleep(wait)


def query(search):
    params = {"search": search, "limit": 10}
    if KEY:
        params["api_key"] = KEY
    url = API + "?" + urllib.parse.urlencode(params)
    for attempt in range(4):
        throttle()
        try:
            with urllib.request.urlopen(url, timeout=30) as r:
                return json.load(r).get("results", [])
        except urllib.error.HTTPError as e:
            if e.code == 404:
                return []
            if e.code == 429:
                time.sleep(5 * (attempt + 1))
                continue
            if e.code == 403:
                print("HTTP 403 - check OPENFDA_API_KEY.")
                raise SystemExit(1)
            time.sleep(2)
        except Exception:
            time.sleep(2)
    return []


def clean(text):
    text = re.sub(r"\s+", " ", text).strip()
    text = re.sub(r"^\d*\.?\s*CONTRAINDICATIONS?:?\s*", "", text, flags=re.I)
    return text.strip()


def pick(results):
    saw_none = False
    for lab in results:
        raw = lab.get("contraindications")
        if not raw:
            continue
        text = clean(" ".join(raw))
        if not text:
            continue
        if re.fullmatch(r"none\.?", text, flags=re.I):
            saw_none = True
            continue
        return "has_text", text
    return ("none_stated", "None.") if saw_none else ("no_data", "")


def strip_suffix(name):
    return SUFFIX_RE.sub("", name).strip()


def lookup_one(name):
    name = name.replace('"', "").strip()
    candidates = [name]
    stripped = strip_suffix(name)
    if stripped != name:
        candidates.append(stripped)

    # Pass A: prescription-only, generic then brand, original then stripped name
    for cand in candidates:
        for field in ("openfda.generic_name", "openfda.brand_name"):
            search = '%s:"%s"%s' % (field, cand, RX_FILTER)
            status, text = pick(query(search))
            if status != "no_data":
                return status, text

    # Pass B: no prescription filter (catches OTC-labeled or discontinued brands)
    for cand in candidates:
        for field in ("openfda.brand_name", "openfda.generic_name"):
            search = '%s:"%s"' % (field, cand)
            status, text = pick(query(search))
            if status != "no_data":
                return status, text

    return "no_data", ""


def lookup(name):
    if "/" not in name:
        return name, *lookup_one(name)
    parts = [p.strip() for p in name.split("/") if p.strip()]
    got = []
    for p in parts:
        st, tx = lookup_one(p)
        if st == "has_text":
            got.append("%s: %s" % (p, tx))
    if got:
        return name, "has_text", " | ".join(got)
    return name, "no_data", ""


def load_cache():
    cache = {}
    if os.path.exists(CACHE):
        with open(CACHE, encoding="utf-8") as f:
            for line in f:
                try:
                    d = json.loads(line)
                    cache[d["n"]] = (d["s"], d["t"])
                except Exception:
                    pass
    return cache


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        raise SystemExit(1)
    if not KEY:
        print("OPENFDA_API_KEY is not set - export it before running.")
        raise SystemExit(1)

    src, dst = sys.argv[1], sys.argv[2]
    limit = None
    if "--limit" in sys.argv:
        limit = int(sys.argv[sys.argv.index("--limit") + 1])
    workers = 8
    if "--workers" in sys.argv:
        workers = int(sys.argv[sys.argv.index("--workers") + 1])

    with open(src, newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    fields = ["genericName", "contraindications", "status"]

    cache = load_cache()
    todo = [r["genericName"] for r in rows
            if r["status"] == "no_data" and r["genericName"] not in cache]
    if limit:
        todo = todo[:limit]
    print("to fetch:", len(todo), "| already cached:", len(cache),
          "| workers:", workers)

    t0 = time.time()
    done = 0
    write_lock = threading.Lock()
    try:
        with open(CACHE, "a", encoding="utf-8") as cf, \
             ThreadPoolExecutor(max_workers=workers) as ex:
            futures = {ex.submit(lookup, name): name for name in todo}
            for fut in as_completed(futures):
                name, st, tx = fut.result()
                cache[name] = (st, tx)
                with write_lock:
                    cf.write(json.dumps({"n": name, "s": st, "t": tx}) + "\n")
                    cf.flush()
                done += 1
                if done % 50 == 0:
                    elapsed = time.time() - t0
                    rate = done / elapsed
                    remaining = (len(todo) - done) / rate if rate else 0
                    print("%d/%d  (%.0fs elapsed, ~%.0fs left)"
                          % (done, len(todo), elapsed, remaining))
    except KeyboardInterrupt:
        print("\nInterrupted - saving what we have.")

    counts = {"has_text": 0, "none_stated": 0, "no_data": 0}
    with open(dst, "w", newline="", encoding="utf-8") as f:
        w = csv.DictWriter(f, fieldnames=fields)
        w.writeheader()
        for r in rows:
            if r["status"] == "no_data" and r["genericName"] in cache:
                st, tx = cache[r["genericName"]]
                r = {"genericName": r["genericName"],
                     "contraindications": tx, "status": st}
            counts[r["status"]] = counts.get(r["status"], 0) + 1
            w.writerow({k: r[k] for k in fields})
    print("wrote", dst, counts)


if __name__ == "__main__":
    main()
