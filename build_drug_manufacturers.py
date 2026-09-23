import sqlite3
import urllib.request
import urllib.parse
import xml.etree.ElementTree as ET
import json
from concurrent.futures import ThreadPoolExecutor, as_completed

DB_PATH = "app/src/main/assets/database/drugs.db"
NS = {"hl7": "urn:hl7-org:v3"}
WORKERS = 100

conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()
cur.execute("""
CREATE TABLE IF NOT EXISTS drug_manufacturers (
    genericName TEXT PRIMARY KEY,
    setId TEXT,
    brandName TEXT,
    manufacturerName TEXT,
    fetchStatus TEXT
)
""")
conn.commit()

cur.execute("SELECT DISTINCT genericName FROM drugs WHERE genericName != '' ORDER BY genericName")
all_generics = [row[0] for row in cur.fetchall()]
print(f"Found {len(all_generics)} unique generic names.")

cur.execute("SELECT genericName FROM drug_manufacturers")
already_done = set(row[0] for row in cur.fetchall())
todo = [g for g in all_generics if g not in already_done]
print(f"Already processed: {len(already_done)}. Remaining: {len(todo)}")


def http_get(url, timeout=20):
    req = urllib.request.Request(url, headers={"User-Agent": "Medtrixa/1.0"})
    return urllib.request.urlopen(req, timeout=timeout).read()


def get_brand_name(root):
    for el in root.iter("{urn:hl7-org:v3}manufacturedProduct"):
        name_el = el.find("hl7:name", NS)
        if name_el is not None and name_el.text:
            return name_el.text.strip()[:200]
    return ""


def get_manufacturer_name(root):
    for el in root.iter("{urn:hl7-org:v3}representedOrganization"):
        name_el = el.find("hl7:name", NS)
        if name_el is not None and name_el.text:
            return name_el.text.strip()[:200]
    return ""


def fetch_one(generic):
    status = "not_found"
    setid = ""
    brand_name = ""
    manufacturer_name = ""
    try:
        search_url = "https://dailymed.nlm.nih.gov/dailymed/services/v2/spls.json?" + urllib.parse.urlencode(
            {"drug_name": generic, "pagesize": 1}
        )
        search_data = json.loads(http_get(search_url))
        results = search_data.get("data", [])
        if results:
            setid = results[0].get("setid", "")
            xml_url = f"https://dailymed.nlm.nih.gov/dailymed/services/v2/spls/{setid}.xml"
            xml_bytes = http_get(xml_url)
            root = ET.fromstring(xml_bytes)
            brand_name = get_brand_name(root)
            manufacturer_name = get_manufacturer_name(root)
            status = "ok"
    except Exception as e:
        status = f"error: {str(e)[:100]}"
    return (generic, setid, brand_name, manufacturer_name, status)


processed_count = len(already_done)
error_count = 0
batch_size = WORKERS

for start in range(0, len(todo), batch_size):
    batch = todo[start:start + batch_size]
    with ThreadPoolExecutor(max_workers=WORKERS) as executor:
        futures = [executor.submit(fetch_one, g) for g in batch]
        for future in as_completed(futures):
            generic, setid, brand_name, manufacturer_name, status = future.result()
            if status.startswith("error"):
                error_count += 1
            cur.execute("""
                INSERT OR REPLACE INTO drug_manufacturers
                (genericName, setId, brandName, manufacturerName, fetchStatus)
                VALUES (?, ?, ?, ?, ?)
            """, (generic, setid, brand_name, manufacturer_name, status))
            processed_count += 1

    conn.commit()
    print(f"Progress: {processed_count}/{len(all_generics)} (errors: {error_count})")

conn.close()
print(f"Done. Processed {processed_count}/{len(all_generics)} generics, {error_count} errors.")
