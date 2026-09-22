#!/usr/bin/env python3
"""Fix rows misclassified as has_text where the raw openFDA text was
actually just a 'None' variant with a stray section marker, e.g.
'None. None ( 4 )', 'None None (4)', 'None. None.'

This is a local pass over an already-fetched CSV - no network calls,
no re-fetching. Safe to re-run.

Usage: python3 clean_none.py contraindications_v3.csv contraindications_v4.csv
"""
import csv, re, sys

# Matches things like: "None.", "None. None ( 4 )", "None None (4).",
# "None. None." - i.e. one or two "None"s plus optional punctuation
# and an optional bracketed/parenthesised section number.
JUNK_RE = re.compile(
    r"^\s*none\.?\s*(none\.?)?\s*\(?\s*4?\s*\)?\.?\s*$", re.I
)

def main():
    if len(sys.argv) != 3:
        print(__doc__)
        raise SystemExit(1)
    src, dst = sys.argv[1], sys.argv[2]

    with open(src, newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    fields = ["genericName", "contraindications", "status"]

    fixed = 0
    for r in rows:
        if r["status"] == "has_text" and JUNK_RE.match(r["contraindications"] or ""):
            r["contraindications"] = "None."
            r["status"] = "none_stated"
            fixed += 1

    with open(dst, "w", newline="", encoding="utf-8") as f:
        w = csv.DictWriter(f, fieldnames=fields)
        w.writeheader()
        for r in rows:
            w.writerow({k: r[k] for k in fields})

    counts = {"has_text": 0, "none_stated": 0, "no_data": 0}
    for r in rows:
        counts[r["status"]] = counts.get(r["status"], 0) + 1
    print("reclassified:", fixed)
    print("wrote", dst, counts)


if __name__ == "__main__":
    main()
