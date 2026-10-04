#!/usr/bin/env python3
"""
Keep ONLY the pioneer (original) brand for each drug in the app's existing drugs.db.
Only the `brand` table is changed; ingredients, products and labels are untouched.
DRY-RUN BY DEFAULT.

  python apply_pioneer_brands.py ./app/src/main/assets/database/drugs.db pioneer_brands.csv            # preview
  python apply_pioneer_brands.py ./app/src/main/assets/database/drugs.db pioneer_brands.csv --apply    # write

For each drug:
  1. if pioneer_brands.csv (FDA Orange Book / Purple Book) names its original brand -> that single brand;
  2. else, if the database lists exactly one brand for it -> keep that one;
  3. else -> no brand shown.
Backup (when applying): ~/drugs_before_pioneer.db (outside the assets folder).
"""
import csv, os, re, shutil, sqlite3, sys

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


def name_key(generic):
    base = FORM_RE.sub("", (generic or "").strip())
    return frozenset(norm(strip_trailing_salts(p)) for p in re.split(r"\s*[/;]\s*", base) if norm(p))


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    if len(args) != 2:
        sys.exit(__doc__)
    db, csv_path = os.path.expanduser(args[0]), os.path.expanduser(args[1])
    apply = "--apply" in sys.argv
    pioneer = {}
    for r in csv.DictReader(open(csv_path, encoding="utf-8", newline="")):
        pioneer.setdefault(name_key(r["generic"]), r["brand"].strip())
    if apply:
        shutil.copy2(db, os.path.join(os.path.expanduser("~"), "drugs_before_pioneer.db"))
    con = sqlite3.connect(db)
    try:
        ings = con.execute("SELECT rxcui, name FROM ingredient").fetchall()
        old = {}
        for rx, name in con.execute("SELECT ingredientRxcui, name FROM brand ORDER BY name"):
            old.setdefault(rx, []).append(name)
    except sqlite3.OperationalError as e:
        sys.exit(f"This does not look like the new database ({e}).")
    new_rows, n_pioneer, n_single, n_none, shown = [], 0, 0, 0, 0
    for rx, name in ings:
        cur = old.get(rx, [])
        k = name_key(name)
        if k in pioneer:
            pick, n_pioneer = pioneer[k], n_pioneer + 1
        elif len(cur) == 1:
            pick, n_single = cur[0], n_single + 1
        else:
            pick, n_none = None, n_none + (1 if cur else 0)
        if pick:
            new_rows.append((rx, pick))
        if shown < 20 and len(cur) > 1 and pick:
            print(f"{name}: {', '.join(cur[:6])}{'...' if len(cur) > 6 else ''}  ->  {pick}")
            shown += 1
    print(f"Brands before: {sum(len(v) for v in old.values())}  ->  after: {len(new_rows)} "
          f"(pioneer from FDA lists: {n_pioneer}, single existing brand kept: {n_single}, "
          f"drugs left with no brand: {n_none})")
    if apply:
        con.execute("DELETE FROM brand")
        con.executemany("INSERT INTO brand (ingredientRxcui, name) VALUES (?,?)", new_rows)
        con.commit()
        con.execute("VACUUM")
        print("Applied.")
    else:
        print("Preview only - nothing written.")
    con.close()


if __name__ == "__main__":
    main()
