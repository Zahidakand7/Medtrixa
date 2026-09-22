import sqlite3
import urllib.request
import urllib.parse
import xml.etree.ElementTree as ET
import json
import time
import sys

DB_PATH = "app/src/main/assets/database/drugs.db"
NS = {"hl7": "urn:hl7-org:v3"}

SECTION_CODES = {
    "indications": "34067-9",
    "dosage": "34068-7",
    "contraindications": "34070-3",
    "warnings": "43685-7",
    "adverseReactions": "34084-4",
    "pregnancy": "42228-7",
    "mechanism": "43679-0",
}

conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()

cur.execute("""
CREATE TABLE IF NOT EXISTS drug_labels (
    genericName TEXT PRIMARY KEY,
    setId TEXT,
    indications TEXT,
    dosage TEXT,
    contraindications TEXT,
    warnings TEXT,
    adverseReactions TEXT,
    pregnancy TEXT,
    mechanism TEXT,
    fetchStatus TEXT
)
""")
conn.commit()

cur.execute("SELECT DISTINCT genericName FROM drugs WHERE genericName != '' ORDER BY genericName")
all_generics = [row[0] for row in cur.fetchall()]
print(f"Found {len(all_generics)} unique generic names.")

cur.execute("SELECT genericName FROM drug_labels")
already_done = set(row[0] for row in cur.fetchall())
print(f"Already processed: {len(already_done)}")

def http_get(url, timeout=20):
    req = urllib.request.Request(url, headers={"User-Agent": "Medtrixa/1.0"})
    return urllib.request.urlopen(req, timeout=timeout).read()

def get_section_text(root, code):
    for section in root.iter("{urn:hl7-org:v3}section"):
        code_el = section.find("hl7:code", NS)
        if code_el is not None and code_el.get("code") == code:
            texts = []
            for t in section.iter("{urn:hl7-org:v3}text"):
                texts.append("".join(t.itertext()))
            if texts:
                return " ".join(texts).strip()[:4000]
    return ""

processed_count = len(already_done)
error_count = 0

for i, generic in enumerate(all_generics):
    if generic in already_done:
        continue

    status = "not_found"
    setid = ""
    sections = {k: "" for k in SECTION_CODES}

    try:
        search_url = "https://dailymed.nlm.nih.gov/dailymed/services/v2/spls.json?" + urllib.parse.urlencode({"drug_name": generic, "pagesize": 1})
        search_data = json.loads(http_get(search_url))
        results = search_data.get("data", [])
        if results:
            setid = results[0].get("setid", "")
            xml_url = f"https://dailymed.nlm.nih.gov/dailymed/services/v2/spls/{setid}.xml"
            xml_bytes = http_get(xml_url)
            root = ET.fromstring(xml_bytes)
            for key, code in SECTION_CODES.items():
                sections[key] = get_section_text(root, code)
            status = "ok"
    except Exception as e:
        status = f"error: {str(e)[:100]}"
        error_count += 1

    cur.execute("""
        INSERT OR REPLACE INTO drug_labels
        (genericName, setId, indications, dosage, contraindications, warnings, adverseReactions, pregnancy, mechanism, fetchStatus)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, (
        generic, setid,
        sections["indications"], sections["dosage"], sections["contraindications"],
        sections["warnings"], sections["adverseReactions"], sections["pregnancy"], sections["mechanism"],
        status
    ))

    processed_count += 1
    if processed_count % 20 == 0:
        conn.commit()
        print(f"Progress: {processed_count}/{len(all_generics)} (errors: {error_count})")

    time.sleep(0.3)

conn.commit()
conn.close()
print(f"Done. Processed {processed_count}/{len(all_generics)} generics, {error_count} errors.")
