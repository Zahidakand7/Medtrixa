#!/usr/bin/env python3
"""
List drugs that have NO pioneer company in pioneer_brands.csv (read-only, changes nothing).
Run from the same folder as apply_originator_company.py:

  python list_missing_pioneers.py ./app/src/main/assets/database/drugs.db pioneer_brands.csv

Writes ~/missing_pioneers.csv with columns: generic,current_manufacturer
"""
import csv, os, sqlite3, sys

from apply_originator_company import name_key

if len(sys.argv) != 3:
    sys.exit(__doc__)
db, csv_path = os.path.expanduser(sys.argv[1]), os.path.expanduser(sys.argv[2])

have = set()
for r in csv.DictReader(open(csv_path, encoding="utf-8", newline="")):
    if r.get("company", "").strip():
        have.add(name_key(r["generic"]))

con = sqlite3.connect(f"file:{db}?mode=ro", uri=True)
rows = con.execute("SELECT i.name, l.manufacturer FROM label l "
                   "JOIN ingredient i ON i.rxcui = l.ingredientRxcui ORDER BY i.name").fetchall()
con.close()

missing = [(n, m) for n, m in rows if name_key(n) not in have]
out = os.path.join(os.path.expanduser("~"), "missing_pioneers.csv")
with open(out, "w", encoding="utf-8", newline="") as f:
    w = csv.writer(f)
    w.writerow(["generic", "current_manufacturer"])
    w.writerows(missing)
print(f"{len(missing)} of {len(rows)} drugs have no pioneer. Saved to {out}")
for n, m in missing[:15]:
    print(f"  {n}  ->  {m}")
