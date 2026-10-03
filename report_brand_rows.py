#!/usr/bin/env python3
"""
READ-ONLY report: which rows in drugs.db are really BRAND names (e.g. Viagra) and which ingredient they belong to.
Nothing in drugs.db is changed.

  python report_brand_rows.py ./app/src/main/assets/database/drugs.db brand_index.csv

Writes brand_rows_report.csv in the current folder with columns:
  row_name, ingredient, other_ingredients, ingredient_row_exists, rows_for_this_name
Open it, decide what to do (merge under the ingredient, or delete), and tell me - I'll write the change.
"""
import csv, os, re, sqlite3, sys

SALT_WORDS = {"hydrochloride", "hcl", "sodium", "potassium", "sulfate", "sulphate", "phosphate", "acetate", "maleate",
              "mesylate", "besylate", "tartrate", "citrate", "succinate", "fumarate", "bromide", "hydrobromide",
              "bitartrate", "nitrate", "chloride", "dihydrate", "monohydrate", "anhydrous", "disodium", "calcium",
              "magnesium", "besilate", "tosylate", "oxalate", "lactate", "gluconate", "carbonate", "hydroxide",
              "dipropionate", "propionate", "valerate", "furoate", "acetonide", "hemihydrate", "trihydrate"}
FORM_RE = re.compile(r"\s+(XR|ER|XL|CR|SR)$", re.I)


def norm(s):
    return re.sub(r"[^a-z0-9]", "", (s or "").lower())


def strip_trailing_salts(p):
    t = p.strip().split()
    while len(t) > 1 and t[-1].lower().strip(",") in SALT_WORDS:
        t.pop()
    return " ".join(t).strip(" ,")


def key_of(generic):
    base = FORM_RE.sub("", generic.strip())
    return frozenset(norm(strip_trailing_salts(p)) for p in re.split(r"[/;]", base) if norm(p))


def main():
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    db, idx_path = sys.argv[1:]
    index = {}
    for r in csv.DictReader(open(os.path.expanduser(idx_path), encoding="utf-8", newline="")):
        index[norm(r["brand"])] = [g.strip() for g in r["generics"].split(";") if g.strip()]
    con = sqlite3.connect(f"file:{db}?mode=ro", uri=True)  # read-only
    table = None
    for (t,) in con.execute("SELECT name FROM sqlite_master WHERE type='table'").fetchall():
        cols = {norm(c[1]): c[1] for c in con.execute(f'PRAGMA table_info("{t}")')}
        if "genericname" in cols and "brandname" in cols:
            table = (t, cols["genericname"])
            break
    if not table:
        sys.exit("No drug table found.")
    t, g = table
    names = con.execute(f'SELECT "{g}", COUNT(*) FROM "{t}" GROUP BY "{g}"').fetchall()
    db_keys = {key_of(n) for n, _ in names if n}
    report = []
    for name, cnt in names:
        if not name:
            continue
        gens = index.get(norm(name))
        if not gens or key_of(name) in {key_of(x) for x in gens}:
            continue
        main_gen = gens[0]
        report.append([name, main_gen, "; ".join(gens[1:]), "yes" if key_of(main_gen) in db_keys else "NO", cnt])
    report.sort(key=lambda r: r[0].lower())
    with open("brand_rows_report.csv", "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["row_name", "ingredient", "other_ingredients", "ingredient_row_exists", "rows_for_this_name"])
        w.writerows(report)
    yes = sum(1 for r in report if r[3] == "yes")
    print(f"{len(names)} distinct names in the table; {len(report)} are brand names "
          f"({yes} whose ingredient row already exists, {len(report) - yes} whose ingredient row does not).")
    for r in report[:25]:
        print(f"  {r[0]} -> {r[1]}  [ingredient row: {r[3]}]")
    print("Full list: brand_rows_report.csv  (database not modified)")


if __name__ == "__main__":
    main()
