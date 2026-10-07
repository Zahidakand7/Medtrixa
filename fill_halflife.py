import sqlite3, requests, re, csv, time, os, sys
DB = "app/src/main/assets/database/drugs.db"
OUT = "halflife_review.csv"
con = sqlite3.connect(DB)
rows = con.execute("""SELECT i.rxcui, i.name FROM ingredient i
  JOIN label l ON l.ingredientRxcui = i.rxcui
  WHERE l.halfLife IS NULL OR trim(l.halfLife) = ''""").fetchall()
val = re.compile(r'(\d+(?:\.\d+)?)(?:\s*(?:±|\+/-)\s*\d+(?:\.\d+)?)?((?:\s*(?:to|-|–)\s*\d+(?:\.\d+)?)?)\s*(minutes?|min|hours?|hrs?|h|days?)\b', re.I)
FIELDS = ["pharmacokinetics", "clinical_pharmacology", "description"]

def find(name):
    r = requests.get("https://api.fda.gov/drug/label.json",
        params={"search": f'openfda.generic_name:"{name}"', "limit": 5}, timeout=10)
    if r.status_code != 200:
        return None
    for res in r.json().get("results", []):
        for f in FIELDS:
            text = " ".join(res.get(f, []))
            for s in re.split(r'(?<=[.;])\s+', text):
                if re.search(r'half[- ]?life', s, re.I):
                    m = val.search(s)
                    if m:
                        ev = " ".join(s.split())[:250]
                        return f"{m.group(1)}{m.group(2)} {m.group(3)}", ev
    return None

done = set()
new = not os.path.exists(OUT)
if not new:
    with open(OUT) as fh:
        done = {r[0] for r in csv.reader(fh)}
fh = open(OUT, "a", newline="")
w = csv.writer(fh)
if new:
    w.writerow(["rxcui", "name", "proposed", "evidence"])
for n, (rxcui, name) in enumerate(rows, 1):
    if str(rxcui) in done:
        continue
    try:
        hit = find(name)
        status = hit[0] if hit else "-"
    except Exception as e:
        hit, status = None, "ERR " + type(e).__name__
    if hit:
        w.writerow([rxcui, name, hit[0], hit[1]])
        fh.flush()
    print(f"{n}/{len(rows)} {name}: {status}")
    sys.stdout.flush()
    time.sleep(0.3)
print("done")
