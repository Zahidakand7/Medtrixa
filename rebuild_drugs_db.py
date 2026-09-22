import json
import sqlite3
import re

DB_PATH = "app/src/main/assets/database/drugs.db"

FORM_ABBR = {
    "tab": "Tablet", "tabs": "Tablet", "cap": "Capsule", "caps": "Capsule",
    "sol": "Solution", "susp": "Suspension", "syr": "Syrup", "inj": "Injection",
    "cream": "Cream", "oint": "Ointment", "gel": "Gel", "patch": "Patch",
    "supp": "Suppository", "spray": "Spray", "drops": "Drops", "lotion": "Lotion",
    "powder": "Powder", "chewable tab": "Chewable Tablet", "er tab": "Extended-Release Tablet",
    "disintegrating tab": "Disintegrating Tablet", "film": "Film",
}

def expand_form(form_text):
    key = form_text.strip().lower()
    return FORM_ABBR.get(key, form_text.strip())

def split_strength_form(entry):
    m = re.match(r'^([\d.]+(?:-[\d.]+)?\s*[a-zA-Z/%]+(?:/[a-zA-Z0-9.]+)?)\s+(.+)$', entry.strip())
    if m:
        return m.group(1).strip(), expand_form(m.group(2))
    return "", expand_form(entry)

def split_display_name(name):
    m = re.match(r'^(.+?)\s*\(([^)]+)\)\s*$', name.strip())
    if m:
        generic = m.group(1).strip()
        route_form = m.group(2).strip()
        route = route_form.split()[0] if route_form else ""
        return generic, route
    return name.strip(), ""

print("Loading raw RxTerms data...")
with open("rxterms_raw.json") as f:
    raw = json.load(f)
print(f"Loaded {len(raw)} unique drug entries.")

conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()

cur.execute("DROP TABLE IF EXISTS drugs")
cur.execute("DROP TABLE IF EXISTS drug_labels")
cur.execute("""
CREATE TABLE drugs (
    id TEXT NOT NULL PRIMARY KEY,
    genericName TEXT NOT NULL,
    formulation TEXT NOT NULL,
    strength TEXT NOT NULL,
    route TEXT NOT NULL,
    rxcui TEXT NOT NULL,
    searchKey TEXT NOT NULL
)
""")

row_count = 0
for rxcui, entry in raw.items():
    generic, route = split_display_name(entry.get("display_name", ""))
    if not generic:
        continue
    strengths_forms = entry.get("strengths_forms", [])
    if not strengths_forms:
        row_id = f"{rxcui}-0"
        key = f"{generic.lower()}|{route.lower()}"
        cur.execute(
            "INSERT OR IGNORE INTO drugs (id, genericName, formulation, strength, route, rxcui, searchKey) VALUES (?, ?, ?, ?, ?, ?, ?)",
            (row_id, generic, "", "", route, rxcui, key)
        )
        row_count += 1
    else:
        for idx, sf in enumerate(strengths_forms):
            strength, formulation = split_strength_form(sf)
            row_id = f"{rxcui}-{idx}"
            key = f"{generic.lower()}|{formulation.lower()}|{route.lower()}"
            cur.execute(
                "INSERT OR IGNORE INTO drugs (id, genericName, formulation, strength, route, rxcui, searchKey) VALUES (?, ?, ?, ?, ?, ?, ?)",
                (row_id, generic, formulation, strength, route, rxcui, key)
            )
            row_count += 1

conn.commit()

cur.execute("SELECT COUNT(*) FROM drugs")
total = cur.fetchone()[0]
cur.execute("SELECT COUNT(DISTINCT genericName) FROM drugs")
distinct_generics = cur.fetchone()[0]

# Recreate drug_labels table (fresh, to be repopulated against the new clean names)
cur.execute("""
CREATE TABLE drug_labels (
    genericName TEXT NOT NULL PRIMARY KEY,
    setId TEXT,
    indications TEXT,
    adultDose TEXT,
    adultAdministration TEXT,
    childDose TEXT,
    childAdministration TEXT,
    renalHepaticDose TEXT,
    contraindications TEXT,
    warnings TEXT,
    adverseReactions TEXT,
    pregnancy TEXT,
    mechanism TEXT,
    fetchStatus TEXT
)
""")
conn.commit()
conn.execute("VACUUM")
conn.close()

print(f"Done. {total} drug rows, {distinct_generics} distinct generic names.")
print("drug_labels table recreated empty, ready for DailyMed refetch.")
