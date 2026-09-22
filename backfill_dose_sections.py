import sqlite3
import urllib.request
import xml.etree.ElementTree as ET
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed

DB_PATH = "app/src/main/assets/database/drugs.db"
NS = {"hl7": "urn:hl7-org:v3"}
WORKERS = 48
CHILD_CODE = "34081-0"
RENAL_CODE = "88828-9"
HEPATIC_CODE = "88829-7"

db_lock = threading.Lock()
conn = sqlite3.connect(DB_PATH, check_same_thread=False)
cur = conn.cursor()

cur.execute("SELECT genericName, setId FROM drug_labels WHERE fetchStatus = 'ok' AND setId != '' AND (childDose IS NULL OR childDose = '')")
rows = cur.fetchall()
print(f"Backfilling {len(rows)} entries...", flush=True)

def http_get(url, timeout=10):
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

def fetch_one(generic, setid):
    try:
        xml_url = f"https://dailymed.nlm.nih.gov/dailymed/services/v2/spls/{setid}.xml"
        root = ET.fromstring(http_get(xml_url))
        child = get_section_text(root, CHILD_CODE)
        renal_text = get_section_text(root, RENAL_CODE)
        hepatic_text = get_section_text(root, HEPATIC_CODE)
        parts = []
        if renal_text:
            parts.append("Renal impairment: " + renal_text)
        if hepatic_text:
            parts.append("Hepatic impairment: " + hepatic_text)
        renal_hepatic = " ".join(parts)[:4000]
        return generic, child, renal_hepatic
    except Exception:
        return generic, "", ""

processed = 0
with ThreadPoolExecutor(max_workers=WORKERS) as executor:
    futures = {executor.submit(fetch_one, g, s): g for g, s in rows}
    for future in as_completed(futures):
        generic, child, renal_hepatic = future.result()
        with db_lock:
            cur.execute("UPDATE drug_labels SET childDose = ?, renalHepaticDose = ? WHERE genericName = ?", (child, renal_hepatic, generic))
            processed += 1
            if processed % 20 == 0:
                conn.commit()
                print(f"Backfill progress: {processed}/{len(rows)}", flush=True)

conn.commit()
conn.close()
print(f"Backfill done. {processed}/{len(rows)}.", flush=True)
