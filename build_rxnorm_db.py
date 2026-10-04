#!/usr/bin/env python3
"""
Build a FRESH drugs.db from RxNorm only (tables ingredient / product / brand / label) for the app's Room schema v5.
Nothing from any older database is read or carried over. The label table is created empty.

  python build_rxnorm_db.py ~/storage/downloads/RxNorm_full_prescribe_09082026.zip ~/drugs_fresh.db
  (reads only rrf/RXNCONSO.RRF, straight from the zip)

Then copy it into the app:  cp ~/drugs_fresh.db ./app/src/main/assets/database/drugs.db
"""
import io, os, re, sqlite3, sys, zipfile
from collections import Counter, defaultdict

DB_VERSION = 5

SALT_WORDS = {"hydrochloride", "hcl", "sodium", "potassium", "sulfate", "sulphate", "phosphate", "acetate", "maleate",
              "mesylate", "besylate", "tartrate", "citrate", "succinate", "fumarate", "bromide", "hydrobromide",
              "bitartrate", "nitrate", "chloride", "dihydrate", "monohydrate", "anhydrous", "disodium", "calcium",
              "magnesium", "besilate", "tosylate", "oxalate", "lactate", "gluconate", "carbonate", "hydroxide",
              "dipropionate", "propionate", "valerate", "furoate", "acetonide", "hemihydrate", "trihydrate"}
FORM_RE = re.compile(r"\s+(XR|ER|XL|CR|SR)$", re.I)
ROUTES = {"oral": "Oral", "sublingual": "Sublingual", "buccal": "Buccal", "topical": "Topical",
          "ophthalmic": "Ophthalmic", "otic": "Otic", "nasal": "Nasal", "rectal": "Rectal", "vaginal": "Vaginal",
          "inhalation": "Inhalation", "transdermal": "Transdermal", "intravenous": "Intravenous",
          "intramuscular": "Intramuscular", "subcutaneous": "Subcutaneous", "intrathecal": "Intrathecal",
          "intravitreal": "Intravitreal", "urethral": "Urethral", "dental": "Dental", "mucosal": "Mucosal"}
UNITS = {"MG": "mg", "MCG": "mcg", "G": "g", "ML": "mL", "UNT": "units", "MEQ": "mEq", "HR": "hr", "L": "L",
         "ACTUAT": "actuation", "MMOL": "mmol", "KG": "kg", "IU": "IU", "CELL": "cells"}


def norm(s):
    return re.sub(r"[^a-z0-9]", "", (s or "").lower())


def strip_trailing_salts(p):
    t = p.strip().split()
    while len(t) > 1 and t[-1].lower().strip(",") in SALT_WORDS:
        t.pop()
    return " ".join(t).strip(" ,")


def name_key(generic):
    """Order-independent key of an ingredient/combination name, ignoring salts and XR/ER suffixes."""
    base = FORM_RE.sub("", (generic or "").strip())
    return frozenset(norm(strip_trailing_salts(p)) for p in re.split(r"\s*[/;]\s*", base) if norm(p))


def display(name):
    return " ".join(w[:1].upper() + w[1:] for w in name.split(" "))


def open_conso(path):
    path = os.path.expanduser(path)
    if path.lower().endswith(".zip"):
        zf = zipfile.ZipFile(path)
        member = next(n for n in zf.namelist() if n.endswith("RXNCONSO.RRF"))
        return io.TextIOWrapper(zf.open(member), encoding="utf-8", errors="replace")
    return open(path, encoding="utf-8", errors="replace")


STRENGTH_RE = re.compile(r"^(\d[\d.,]*)\s?([A-Za-z%]+)(?:/(\d[\d.,]*)?\s?([A-Za-z%]+))?\s*(.*)$")
LEAD_QTY_RE = re.compile(r"^\d[\d.,]*\s+[A-Z]{1,8}\s+")


def fmt_strength(num, unit, per_num, per_unit):
    u = UNITS.get(unit.upper(), unit.lower() if unit.isalpha() else unit)
    s = f"{num} {u}"
    if per_unit:
        pu = UNITS.get(per_unit.upper(), per_unit.lower())
        s += f"/{(per_num + ' ') if per_num and per_num != '1' else ''}{pu}"
    return s


def split_form(rest):
    """'Delayed Release Oral Capsule' -> (route 'Oral', form 'Delayed Release Capsule')."""
    words = rest.split()
    route = None
    kept = []
    for w in words:
        if route is None and w.lower() in ROUTES:
            route = ROUTES[w.lower()]
        else:
            kept.append(w)
    form = " ".join(kept).strip() or None
    return route, (form or (rest.strip() or None))


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    if len(args) != 2:
        sys.exit(__doc__)
    zip_path, out_db = args
    out_db = os.path.expanduser(out_db)
    if os.path.exists(out_db):
        os.remove(out_db)

    # ---- 1. RXNCONSO ----
    ing, minn, scd_sbd = {}, {}, []
    for line in open_conso(zip_path):
        p = line.split("|")
        if len(p) < 15 or p[11] != "RXNORM":
            continue
        tty, rxcui, s = p[12], p[0], p[14]
        if tty == "IN" and rxcui not in ing:
            ing[rxcui] = s
        elif tty == "MIN" and rxcui not in minn:
            minn[rxcui] = s
        elif tty in ("SCD", "SBD"):
            scd_sbd.append((rxcui, tty, s))
    print(f"RxNorm: {len(ing)} ingredients, {len(minn)} combinations, {len(scd_sbd)} clinical drugs")

    in_by_name = {}
    for r, n in ing.items():
        in_by_name.setdefault(n.lower(), int(r))
    max_words = max(len(n.split()) for n in in_by_name)

    def resolve_in(text):
        """Longest ingredient name that starts the text (word boundary). Returns (rxcui, rest)."""
        words = text.split()
        for k in range(min(max_words, len(words)), 0, -1):
            cand = " ".join(words[:k]).lower()
            if cand in in_by_name:
                return in_by_name[cand], " ".join(words[k:])
        return None, text

    min_by_set = {}
    for r, n in minn.items():
        ids = []
        for part in n.split(" / "):
            i = in_by_name.get(part.strip().lower())
            if i is None:
                ids = None
                break
            ids.append(i)
        if ids:
            min_by_set[frozenset(ids)] = int(r)

    # ---- 2. products + brands from SCD/SBD names ----
    products, brands = {}, defaultdict(dict)
    parsed = 0
    failed = []
    for rxcui, tty, s in scd_sbd:
        brand = None
        m = re.search(r"\s\[([^\]]+)\]\s*$", s)
        if m:
            brand = m.group(1).strip()
            s = s[: m.start()]
        comps = s.split(" / ")
        ids, strengths, route, form = [], [], None, None
        ok = True
        for ci, comp in enumerate(comps):
            comp = LEAD_QTY_RE.sub("", comp.strip())
            i, rest = resolve_in(comp)
            if i is None:
                ok = False
                break
            rw = rest.split()  # skip salt/precise-ingredient words ("hydrochloride", "(USP)") up to the strength
            for j, w in enumerate(rw[:5]):
                if w[:1].isdigit():
                    rest = " ".join(rw[j:])
                    break
            sm = STRENGTH_RE.match(rest)
            if not sm:
                ok = False
                break
            num, unit, pnum, punit, tail = sm.groups()
            ids.append(i)
            strengths.append(fmt_strength(num, unit, pnum, punit))
            if ci == len(comps) - 1:
                route, form = split_form(tail)
        if not ok:
            if len(failed) < 400:
                failed.append(s)
            continue
        key = frozenset(ids)
        target = ids[0] if len(key) == 1 else min_by_set.get(key)
        if target is None:
            if len(failed) < 400:
                failed.append("[no combination] " + s)
            continue
        parsed += 1
        products[int(rxcui)] = (target, " / ".join(strengths), form, route)
        if brand:
            brands[target].setdefault(brand.lower(), brand)
    print(f"Clinical drugs linked to an ingredient: {parsed} of {len(scd_sbd)}")
    if "--show-failures" in sys.argv:
        for f in failed[:40]:
            print("   unparsed:", f)

    classes, labels, mfrs, old_brands = {}, {}, {}, {}

    # ---- 4. write the new DB (schema matches the Room entities exactly) ----
    con = sqlite3.connect(out_db)
    con.executescript("""
    CREATE TABLE ingredient (rxcui INTEGER NOT NULL, name TEXT NOT NULL, kind TEXT NOT NULL, drugClass TEXT, PRIMARY KEY(rxcui));
    CREATE INDEX index_ingredient_name ON ingredient (name);
    CREATE TABLE product (rxcui INTEGER NOT NULL, ingredientRxcui INTEGER NOT NULL, strength TEXT, form TEXT, route TEXT, PRIMARY KEY(rxcui));
    CREATE INDEX index_product_ingredientRxcui ON product (ingredientRxcui);
    CREATE TABLE brand (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ingredientRxcui INTEGER NOT NULL, name TEXT NOT NULL);
    CREATE INDEX index_brand_ingredientRxcui ON brand (ingredientRxcui);
    CREATE INDEX index_brand_name ON brand (name);
    CREATE TABLE label (ingredientRxcui INTEGER NOT NULL, setId TEXT, manufacturer TEXT, otherManufacturers TEXT,
        effectiveDate TEXT, indications TEXT, adultDose TEXT, adultAdministration TEXT, childDose TEXT,
        renalHepaticDose TEXT, contraindications TEXT, warnings TEXT, adverseReactions TEXT, pregnancy TEXT,
        mechanism TEXT, PRIMARY KEY(ingredientRxcui));
    """)
    rows, keyed = [], {}
    for r, n in ing.items():
        nm = display(n)
        rows.append((int(r), nm, "IN", classes.get(name_key(nm))))
        keyed.setdefault(name_key(nm), int(r))
    for r, n in minn.items():
        nm = "/".join(display(x.strip()) for x in n.split(" / "))
        rows.append((int(r), nm, "MIN", classes.get(name_key(nm))))
        keyed.setdefault(name_key(nm), int(r))
    con.executemany("INSERT INTO ingredient VALUES (?,?,?,?)", rows)
    con.executemany("INSERT INTO product VALUES (?,?,?,?,?)", [(r, *v) for r, v in products.items()])
    con.executemany("INSERT INTO brand (ingredientRxcui, name) VALUES (?,?)",
                    [(t, b) for t, d in brands.items() for b in d.values()])
    con.execute(f"PRAGMA user_version = {DB_VERSION}")
    con.commit()
    con.execute("VACUUM")
    n_i = con.execute("SELECT COUNT(*) FROM ingredient").fetchone()[0]
    n_p = con.execute("SELECT COUNT(*) FROM product").fetchone()[0]
    n_b = con.execute("SELECT COUNT(*) FROM brand").fetchone()[0]
    print(f"\nFresh database {out_db}: {n_i} ingredients, {n_p} products, {n_b} brands, label table empty")
    for q in ("clopidogrel", "metformin", "ethambutol", "sildenafil"):
        r = con.execute("SELECT i.rxcui, i.name, (SELECT group_concat(name, ', ') FROM brand WHERE ingredientRxcui=i.rxcui), "
                        "(SELECT COUNT(*) FROM product WHERE ingredientRxcui=i.rxcui), "
                        "(SELECT COUNT(*) FROM label WHERE ingredientRxcui=i.rxcui) "
                        "FROM ingredient i WHERE name = ? COLLATE NOCASE", (q,)).fetchone()
        print("  check:", q, "->", r)
    con.close()


if __name__ == "__main__":
    main()
