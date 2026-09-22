import csv, requests, time, os, threading, sqlite3
from concurrent.futures import ThreadPoolExecutor, as_completed

OUT_PATH = os.path.expanduser("~/Medtrixa/app/src/main/assets/contraindications.csv")
DB_PATH = os.path.expanduser("~/Medtrixa/app/src/main/assets/database/drugs.db")
API_KEY = "q0GQGiYeaxbaOV0C8dzST72Lh7GASQFtQFhaxFsu"
WORKERS = 35

# Load existing results
results = {}
with open(OUT_PATH, "r", encoding="utf-8") as f:
    reader = csv.reader(f)
    header = next(reader)
    for row in reader:
        if row:
            results[row[0]] = row[1] if len(row) > 1 else ""

to_retry = [name for name, text in results.items() if not text.strip()]
print(f"Retrying with rxcui fallback: {len(to_retry)} entries")

# Build genericName -> rxcui lookup
conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()
cur.execute("SELECT genericName, rxcui FROM drugs WHERE rxcui IS NOT NULL AND rxcui != ''")
rxcui_map = {}
for name, rxcui in cur.fetchall():
    if name not in rxcui_map:
        rxcui_map[name] = rxcui.split(",")[0].strip()  # first rxcui if multiple
conn.close()

session = requests.Session()
write_lock = threading.Lock()

def query_rxcui(rxcui):
    url = "https://api.fda.gov/drug/label.json"
    params = {
        "search": f'openfda.rxcui:"{rxcui}"',
        "limit": 1,
        "api_key": API_KEY
    }
    for attempt in range(3):
        try:
            resp = session.get(url, params=params, timeout=20)
            if resp.status_code == 200:
                data = resp.json()
                res = data.get("results", [])
                if res:
                    c = res[0].get("contraindications", [])
                    if c:
                        return " ".join(c).replace("\n", " ").strip()
                return None
            elif resp.status_code == 429:
                time.sleep(2 * (attempt + 1))
                continue
            else:
                return None
        except Exception:
            time.sleep(1 * (attempt + 1))
            continue
    return None

def fetch_one(name):
    rxcui = rxcui_map.get(name)
    if not rxcui:
        return name, ""
    text = query_rxcui(rxcui)
    return name, text or ""

candidates = [n for n in to_retry if n in rxcui_map]
print(f"Of those, {len(candidates)} have an rxcui to try")

completed = 0
with ThreadPoolExecutor(max_workers=WORKERS) as executor:
    futures = {executor.submit(fetch_one, name): name for name in candidates}
    for future in as_completed(futures):
        name, text = future.result()
        with write_lock:
            if text:
                results[name] = text
            completed += 1
            if completed % 50 == 0:
                print(f"{completed}/{len(candidates)} retried...")

with open(OUT_PATH, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(header)
    for name, text in results.items():
        writer.writerow([name, text])

final_with_data = sum(1 for t in results.values() if t.strip())
print(f"Done. Total with data now: {final_with_data} / {len(results)}")
