#!/usr/bin/env python3
"""
Show ONLY the originator company in the Manufacturer tile (no list of 30 generic makers).
Changes only two columns of the `label` table (manufacturer, otherManufacturers). DRY-RUN BY DEFAULT.

  python apply_originator_company.py ./app/src/main/assets/database/drugs.db pioneer_brands.csv            # preview
  python apply_originator_company.py ./app/src/main/assets/database/drugs.db pioneer_brands.csv --apply    # write

For each drug that has a label row:
  - if pioneer_brands.csv names its original product's company -> manufacturer = that company
  - otherwise the first existing manufacturer is kept
  - otherManufacturers is cleared (so no "+N more" and no long list)
Note: the company is the one listed on the FDA record of the pioneer product (the current holder of that
original application), which can differ from the very first discoverer after takeovers.
Backup (when applying): ~/drugs_before_originator.db (outside the assets folder).
"""
import csv, os, re, shutil, sqlite3, sys

SALT_WORDS = {"hydrochloride", "hcl", "sodium", "potassium", "sulfate", "sulphate", "phosphate", "acetate", "maleate",
              "mesylate", "besylate", "tartrate", "citrate", "succinate", "fumarate", "bromide", "hydrobromide",
              "bitartrate", "nitrate", "chloride", "dihydrate", "monohydrate", "anhydrous", "disodium", "calcium",
              "magnesium", "besilate", "tosylate", "oxalate", "lactate", "gluconate", "carbonate", "hydroxide",
              "dipropionate", "propionate", "valerate", "furoate", "acetonide", "hemihydrate", "trihydrate"}
FORM_RE = re.compile(r"\s+(XR|ER|XL|CR|SR)$", re.I)
FIX = {"Llc": "LLC", "Lp": "LP", "Usa": "USA", "U.s.": "U.S.", "Ag": "AG", "Gmbh": "GmbH", "Nv": "NV", "Plc": "PLC",
       "Sa": "SA", "Ab": "AB", "Bv": "BV", "Kg": "KG", "Ppc": "PPC", "Usa,": "USA,"}


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


def tidy_company(c):
    c = re.split(r"\s+Div(?:\s|$)", c.strip(), maxsplit=1)[0]  # "Mcneil Consumer Healthcare Div Mcneil Ppc Inc"
    return " ".join(FIX.get(w, w) for w in c.split())


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    if len(args) != 2:
        sys.exit(__doc__)
    db, csv_path = os.path.expanduser(args[0]), os.path.expanduser(args[1])
    apply = "--apply" in sys.argv
    company = {}
    for r in csv.DictReader(open(csv_path, encoding="utf-8", newline="")):
        if r.get("company", "").strip():
            company.setdefault(name_key(r["generic"]), tidy_company(r["company"]))
    if apply:
        shutil.copy2(db, os.path.join(os.path.expanduser("~"), "drugs_before_originator.db"))
    con = sqlite3.connect(db)
    try:
        rows = con.execute("SELECT l.ingredientRxcui, i.name, l.manufacturer, l.otherManufacturers "
                           "FROM label l JOIN ingredient i ON i.rxcui = l.ingredientRxcui").fetchall()
    except sqlite3.OperationalError as e:
        sys.exit(f"This does not look like the new database ({e}).")
    set_pioneer = kept_first = shown = 0
    for rx, name, man, others in rows:
        k = name_key(name)
        if k in company:
            new = company[k]
            set_pioneer += 1
        else:
            new = man
            kept_first += 1
        if shown < 20 and (new != man or (others or "").strip()):
            print(f"{name}: {man} (+{len([x for x in (others or '').split(';') if x.strip()])} others)  ->  {new}")
            shown += 1
        if apply:
            con.execute("UPDATE label SET manufacturer=?, otherManufacturers=NULL WHERE ingredientRxcui=?", (new, rx))
    print(f"{len(rows)} label rows: originator company set for {set_pioneer}, "
          f"first existing manufacturer kept for {kept_first}; all 'other manufacturers' cleared.")
    if apply:
        con.commit()
        con.execute("VACUUM")
        print("Applied.")
    else:
        print("Preview only - nothing written.")
    con.close()


if __name__ == "__main__":
    main()
