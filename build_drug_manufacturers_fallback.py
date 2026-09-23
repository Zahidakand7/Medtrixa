import sqlite3
import urllib.request
import urllib.parse
import json
from concurrent.futures import ThreadPoolExecutor, as_completed

DB_PATH = "app/src/main/assets/database/drugs.db"
WORKERS = 100

conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()

cur.execute("""
    SELECT genericName FROM drug_manufacturers
    WHERE fetchStatus = 'not_found' OR fetchStatus LIKE 'error%'
""")
todo = [row[0] for row in cur.fetchall()]
print(f"Retrying {len(todo)} drugs via openFDA.")


def http_get(url, timeout=20):
    req = urllib.request.Request(url, headers={"User-Agent": "Medtrixa/1.0"})
    return urllib.request.urlopen(req, timeout=timeout).read()


def fetch_one(generic):
    status = "not_found"
    brand_name = ""
    manufacturer_name = ""
    try:
        query = f'openfda.generic_name:"{generic}"'
        url = "https://api.fda.gov/drug/label.json?" + urllib.parse.urlencode({
            "search": query,
            "limit": 1
        })
        data = json.loads(http_get(url))
        results = data.get("results", [])
        if results:
            openfda = results[0].get("openfda", {})
            brands = openfda.get("brand_name", [])
            manufacturers = openfda.get("manufacturer_name", [])
            brand_name = brands[0][:200] if brands else ""
            manufacturer_name = manufacturers[0][:200] if manufacturers else ""
            if brand_name or manufacturer_name:
                status = "ok_fallback"
    except Exception as e:
        status = f"error_fallback: {str(e)[:100]}"
    return (generic, brand_name, manufacturer_name, status)


processed_count = 0
recovered_count = 0
batch_size = WORKERS

for start in range(0, len(todo), batch_size):
    batch = todo[start:start + batch_size]
    with ThreadPoolExecutor(max_workers=WORKERS) as executor:
        futures = [executor.submit(fetch_one, g) for g in batch]
        for future in as_completed(futures):
            generic, brand_name, manufacturer_name, status = future.result()
            if status == "ok_fallback":
                recovered_count += 1
                cur.execute("""
                    UPDATE drug_manufacturers
                    SET brandName = ?, manufacturerName = ?, fetchStatus = ?
                    WHERE genericName = ?
                """, (brand_name, manufacturer_name, status, generic))
            else:
                cur.execute("""
                    UPDATE drug_manufacturers
                    SET fetchStatus = ?
                    WHERE genericName = ?
                """, (status, generic))
            processed_count += 1

    conn.commit()
    print(f"Progress: {processed_count}/{len(todo)} (recovered: {recovered_count})")

conn.close()
print(f"Done. Processed {processed_count}/{len(todo)}, recovered {recovered_count} via openFDA.")
