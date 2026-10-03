#!/usr/bin/env python3
"""
Set the brand name(s) for one drug in drugs.db.

  python set_brand.py ./app/src/main/assets/database/drugs.db Amoxicillin "Amoxil, Moxatag"
"""
import re, sqlite3, sys

if len(sys.argv) != 4:
    sys.exit(__doc__)
db, generic, brands = sys.argv[1:]
norm = lambda s: re.sub(r"[^a-z0-9]", "", s.lower())
con = sqlite3.connect(db)
for (t,) in con.execute("SELECT name FROM sqlite_master WHERE type='table'").fetchall():
    cols = {norm(c[1]): c[1] for c in con.execute(f'PRAGMA table_info("{t}")')}
    if "genericname" in cols and "brandname" in cols:
        g, b = cols["genericname"], cols["brandname"]
        n = con.execute(f'UPDATE "{t}" SET "{b}"=? WHERE LOWER("{g}")=LOWER(?)', (brands, generic)).rowcount
        con.commit()
        print(f"Updated {n} row(s) in {t}: {generic} -> {brands}")
        break
else:
    sys.exit("No table with generic + brand name columns found.")
