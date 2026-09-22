#!/usr/bin/env python3
"""Retry the no_data rows of contraindications_clean.csv against openFDA,
fetching several drugs in parallel for speed.

Usage:
  python3 refetch_fast.py INPUT.csv OUTPUT.csv [--limit N] [--workers 8]

Requires: export OPENFDA_API_KEY=... (240 req/min with a key vs ~40/min without)

Only rows with status == no_data are touched. Everything else is copied as-is.
Progress is cached in refetch_cache.jsonl (shared format with refetch.py),
so you can Ctrl+C and re-run, and it picks up anything already done by
either script. Standard library only.
"""
import csv, json, os, re, sys, time, threading
import urllib.error, urllib.parse, urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed

API = "https://api.fda.gov/drug/label.json"
KEY = os.environ.get("OPENFDA_API_KEY", "")
CACHE = "refetch_cache.jsonl"
RX = 'openfda.product_type:"HUMAN PRESCRIPTION DRUG"'

# openFDA allows 240 req/min with a key (4/sec). Stay a bit under that
# across all worker threads combined using a simple token-bucket lock.
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
                print("HTTP 403 - check OPENFDA_API_KEY is set correctly (not a placeholder).")
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


def lookup_one(name):
    name = name.replace('"', "").strip()
    for field in ("openfda.generic_name", "openfda.brand_name"):
        search = '%s:"%s" AND %s' % (field, name, RX)
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
