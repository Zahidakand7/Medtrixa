import sqlite3
import json
import urllib.request
import zipfile
import io

print("Fetching current OpenFDA download index...")
index = json.loads(urllib.request.urlopen("https://api.fda.gov/download.json").read())
ndc_partitions = index["results"]["drug"]["ndc"]["partitions"]
print(f"Found {len(ndc_partitions)} NDC data file(s).")

conn = sqlite3.connect("drugs.db")
cur = conn.cursor()
cur.execute("""
CREATE TABLE drugs (
    id TEXT PRIMARY KEY,
    genericName TEXT,
    brandName TEXT,
    drugClass TEXT,
    formulation TEXT,
    strength TEXT,
    route TEXT,
    manufacturer TEXT
)
""")

total_inserted = 0
for part in ndc_partitions:
    file_url = part["file"]
    print(f"Downloading {file_url} ...")
    resp = urllib.request.urlopen(file_url)
    z = zipfile.ZipFile(io.BytesIO(resp.read()))
    ndc_data = json.loads(z.read(z.namelist()[0]))["results"]

    for entry in ndc_data:
        try:
            drug_id = entry.get("product_ndc", "")
            generic = entry.get("generic_name", "")
            brand = entry.get("brand_name", "")
            drug_class = ", ".join(entry.get("pharm_class", [])) if entry.get("pharm_class") else ""
            formulation = entry.get("dosage_form", "")
            strength = ""
            if entry.get("active_ingredients"):
                strength = "; ".join(
                    f"{ai.get('name','')} {ai.get('strength','')}"
                    for ai in entry["active_ingredients"]
                )
            route = ", ".join(entry.get("route", [])) if entry.get("route") else ""
            manufacturer = entry.get("labeler_name", "")

            cur.execute("""
                INSERT OR IGNORE INTO drugs
                (id, genericName, brandName, drugClass, formulation, strength, route, manufacturer)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, (drug_id, generic, brand, drug_class, formulation, strength, route, manufacturer))
            total_inserted += 1
        except Exception as e:
            print("Skipped entry due to:", e)

conn.commit()
count = cur.execute('SELECT COUNT(*) FROM drugs').fetchone()[0]
print(f"Inserted {count} drug records.")
conn.close()
print("Done. drugs.db is ready to copy into app/src/main/assets/database/")
