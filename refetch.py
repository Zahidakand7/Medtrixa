#!/usr/bin/env python3
"""Retry the no_data rows of contraindications_clean.csv against openFDA.

Usage:
  python3 refetch.py INPUT.csv OUTPUT.csv [--limit N]

Optional: export OPENFDA_API_KEY=... (free key; without it openFDA allows
only ~1,000 requests/day per IP, so a full run needs a key).

Only rows with status == no_data are touched. Everything else is copied as-is.
Progress is cached in refetch_cache.jsonl, so you can Ctrl+C and re-run.
Standard library only.
"""
import csv, json, os, re, sys, time
import urllib.error, urllib.parse, urllib.request

API = "https://api.fda.gov/drug/label.json"
KEY = os.environ.get("OPENFDA_API_KEY", "")
CACHE = "refetch_cache.jsonl"
RX = 'openfda.product_type:"HUMAN PRESCRIPTION DRUG"'
DELAY = 0.3  # seconds between requests (~200/min, under the 240/min cap)


def query(search):
    params = {"search": search, "limit": 10}
    if KEY:
        params["api_key"] = KEY
    url = API + "?" + urllib.parse.urlencode(params)
    for attempt in range(4):
        try:
            time.sleep(DELAY)
            with urllib.request.urlopen(url, timeout=30) as r:
                return json.load(r).get("results", [])
        except urllib.error.HTTPError as e:
            if e.code == 404:
                return []  # openFDA's "no matches"
            if e.code == 429:
                time.sleep(10 * (attempt + 1))
                continue
            if e.code == 403:
                print("HTTP 403 - daily limit reached? Set OPENFDA_API_KEY.")
                raise SystemExit(1)
            time.sleep(2)
        except Exception:
            time.sleep(3)
    return []


def clean(text):
    text = re.sub(r"\s+", " ", text).strip()
    text = re.sub(r"^\d*\.?\s*CONTRAINDICATIONS?:?\s*", "", text, flags=re.I)
    return text.strip()


def pick(results):
    """Return (status, text) from a list of labels."""
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
        return lookup_one(name)
    # combination drug: look up each part, then merge
    parts = [p.strip() for p in name.split("/") if p.strip()]
    got = []
    for p in parts:
        st, tx = lookup_one(p)
        if st == "has_text":
            got.append("%s: %s" % (p, tx))
    if got:
        return "has_text", " | ".join(got)
    return "no_data", ""


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
    src, dst = sys.argv[1], sys.argv[2]
    limit = None
    if "--limit" in sys.argv:
        limit = int(sys.argv[sys.argv.index("--limit") + 1])

    with open(src, newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    fields = ["genericName", "contraindications", "status"]

    cache = load_cache()
    todo = [r["genericName"] for r in rows
            if r["status"] == "no_data" and r["genericName"] not in cache]
    if limit:
        todo = todo[:limit]
    print("to fetch:", len(todo), "| already cached:", len(cache))

    t0 = time.time()
    try:
        with open(CACHE, "a", encoding="utf-8") as cf:
            for i, name in enumerate(todo, 1):
                st, tx = lookup(name)
                cache[name] = (st, tx)
                cf.write(json.dumps({"n": name, "s": st, "t": tx}) + "\n")
                cf.flush()
                if i % 25 == 0:
                    print("%d/%d  (%.0fs)" % (i, len(todo), time.time() - t0))
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
