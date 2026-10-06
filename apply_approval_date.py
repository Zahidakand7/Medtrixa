import csv, os, shutil, sqlite3, sys
from apply_originator_company import name_key
args = [a for a in sys.argv[1:] if not a.startswith("--")]
db, src = args[0], args[1]
apply = "--apply" in sys.argv
best = {}
for r in csv.DictReader(open(src, encoding="utf-8", newline="")):
    a = (r.get("approved") or "").strip()[:10]
    if len(a) == 10 and a[:4].isdigit():
        k = name_key(r["generic"])
        if k not in best or a < best[k]:
            best[k] = a
con = sqlite3.connect(db)
rows = con.execute("SELECT l.ingredientRxcui, i.name FROM label l JOIN ingredient i ON i.rxcui = l.ingredientRxcui").fetchall()
n = 0
if apply:
    shutil.copy(db, os.path.expanduser("~/drugs_before_approval.db"))
for rx, name in rows:
    d = best.get(name_key(name))
    if d:
        n += 1
        if not apply and n <= 5:
            print(name, "->", d)
    if apply:
        con.execute("UPDATE label SET effectiveDate=? WHERE ingredientRxcui=?", (d, rx))
print(f"{len(rows)} label rows: approval date for {n}, none for {len(rows)-n}.")
if apply:
    con.commit(); print("Applied.")
else:
    print("Preview only - nothing written.")
