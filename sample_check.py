#!/usr/bin/env python3
"""Random sample of remaining no_data rows, plus a quick breakdown of
which ones look like combination (slash) names vs single names.

Usage: python3 sample_check.py contraindications_v2.csv [N]
"""
import csv, random, sys

path = sys.argv[1] if len(sys.argv) > 1 else "contraindications_v2.csv"
n = int(sys.argv[2]) if len(sys.argv) > 2 else 25

with open(path, newline="", encoding="utf-8") as f:
    rows = list(csv.DictReader(f))

no_data = [r["genericName"] for r in rows if r["status"] == "no_data"]
combo = [n_ for n_ in no_data if "/" in n_]
plain = [n_ for n_ in no_data if "/" not in n_]

print("Total no_data:", len(no_data))
print("  combination (slash) names:", len(combo))
print("  single names:", len(plain))

random.seed(2)
print(f"\n{min(n, len(plain))} random single-name misses:")
for name in random.sample(plain, min(n, len(plain))):
    print(" ", name)
