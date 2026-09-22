import sqlite3
import urllib.request
import urllib.parse
import xml.etree.ElementTree as ET
import json
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed

DB_PATH = "app/src/main/assets/database/drugs.db"
NS = {"hl7": "urn:hl7-org:v3"}
WORKERS = 48

SECTION_CODES = {
    "indications": "34067-9",
    "adultDose": "34068-7",
    "childDose": "34081-0",
    "contraindications": "34070-3",
    "warnings": "43685-7",
    "adverseReactions": "34084-4",
    "pregnancy": "42228-7",
    "mechanism": "43679-0",
}
RENAL_CODE = "88828-9"
HEPATIC_CODE = "88829-7"

db_lock = threading.Lock()
conn = sqlite3.connect(DB_PATH, check_same_thread=False)
cur = conn.cursor()

cur.execute("SELECT DISTINCT genericName FROM drugs WHERE genericName != '' ORDER BY genericName")
all_generics = [row[0] for row in cur.fetchall()]

cur.execute("SELECT genericName FROM drug_labels WHERE fetchStatus = 'ok'")
already_done = set(row[0] for row in cur.fetchall())

todo = [g for g in all_generics if g not in already_done]
print(f"Total: {len(all_generics)}, already done: {len(already_done)}, remaining: {len(todo)}", flush=True)

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

def fetch_one(generic):
    status = "not_found"
    setid = ""
    sections = {k: "" for k in SECTION_CODES}
    renal_hepatic = ""
    try:
        search_url = "https://dailymed.nlm.nih.gov/dailymed/services/v2/spls.json?" + urllib.parse.urlencode({"drug_name": generic, "pagesize": 1})
        search_data = json.loads(http_get(search_url))
        results = search_data.get("data", [])
        if results:
            setid = results[0].get("setid", "")
            xml_url = f"https://dailymed.nlm.nih.gov/dailymed/services/v2/spls/{setid}.xml"
            root = ET.fromstring(http_get(xml_url))
            for key, code in SECTION_CODES.items():
                sections[key] = get_section_text(root, code)
            renal_text = get_section_text(root, RENAL_CODE)
            hepatic_text = get_section_text(root, HEPATIC_CODE)
            parts = []
            if renal_text:
                parts.append("Renal impairment: " + renal_text)
            if hepatic_text:
                parts.append("Hepatic impairment: " + hepatic_text)
            renal_hepatic = " ".join(parts)[:4000]
            status = "ok"
    except Exception as e:
        status = f"error: {str(e)[:80]}"
    return generic, setid, sections, renal_hepatic, status

processed = 0
with ThreadPoolExecutor(max_workers=WORKERS) as executor:
    futures = {executor.submit(fetch_one, g): g for g in todo}
    for future in as_completed(futures):
        generic, setid, sections, renal_hepatic, status = future.result()
        with db_lock:
            cur.execute("""
                INSERT OR REPLACE INTO drug_labels
                (genericName, setId, indications, adultDose, childDose, renalHepaticDose,
                 contraindications, warnings, adverseReactions, pregnancy, mechanism, fetchStatus)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                generic, setid,
                sections["indications"], sections["adultDose"], sections["childDose"], renal_hepatic,
                sections["contraindications"], sections["warnings"], sections["adverseReactions"],
                sections["pregnancy"], sections["mechanism"],
                status
            ))
            processed += 1
            if processed % 20 == 0:
                conn.commit()
                print(f"Progress: {processed}/{len(todo)}", flush=True)

conn.commit()
conn.close()
print(f"Done. Processed {processed}/{len(todo)}.", flush=True)
