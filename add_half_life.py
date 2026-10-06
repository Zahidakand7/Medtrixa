import sqlite3, json, re, sys, time, urllib.request, urllib.parse
db = sys.argv[1]
apply = "--apply" in sys.argv
con = sqlite3.connect(db)
cols = [r[1] for r in con.execute("PRAGMA table_info(label)")]
if "halfLife" not in cols:
    if apply:
        con.execute("ALTER TABLE label ADD COLUMN halfLife TEXT")
    rows = con.execute("SELECT ingredientRxcui, setId FROM label WHERE setId IS NOT NULL").fetchall()
else:
    rows = con.execute("SELECT ingredientRxcui, setId FROM label WHERE setId IS NOT NULL AND halfLife IS NULL").fetchall()
if not apply:
    rows = rows[:30]
HL = re.compile(r'half[- ]?life[^.]{0,80}?(\d+(?:\.\d+)?(?:\s*(?:to|-|–)\s*\d+(?:\.\d+)?)?)\s*(hours?|hrs?|h|minutes?|min|days?|weeks?)\b(?!\s*(?:infusion|dose|after|post|prior))', re.I)
VAL = re.compile(r'(\d+(?:\.\d+)?(?:\s*(?:to|-|–)\s*\d+(?:\.\d+)?)?)\s*(hours?|hrs?|h|minutes?|min|days?|weeks?)\b', re.I)
UNIT = {"hr":"hours","hrs":"hours","h":"hours","hour":"hours","hours":"hours","min":"minutes","minute":"minutes","minutes":"minutes","day":"days","days":"days","week":"weeks","weeks":"weeks"}
found = 0
for rx, sid in rows:
    try:
        url = "https://api.fda.gov/drug/label.json?search=" + urllib.parse.quote('set_id:"%s"' % sid) + "&limit=1"
        r = json.load(urllib.request.urlopen(url, timeout=20))["results"][0]
    except Exception:
        continue
    text = " ".join(" ".join(r.get(k, [])) for k in ("pharmacokinetics", "clinical_pharmacology"))
    m = HL.search(text)
    if m:
        val = m.group(1).replace(" to ", "-").replace("–", "-") + " " + UNIT[m.group(2).lower()]
        found += 1
        print(rx, "->", val)
        if apply:
            con.execute("UPDATE label SET halfLife=? WHERE ingredientRxcui=?", (val, rx))
    time.sleep(0.3)
if apply:
    con.commit()
print("rows checked:", len(rows), "half-life found:", found, "(applied)" if apply else "(preview only)")
