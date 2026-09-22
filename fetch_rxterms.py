import urllib.request
import urllib.parse
import json
import time
import string

BASE = "https://clinicaltables.nlm.nih.gov/api/rxterms/v3/search"
results = {}

prefixes = list(string.ascii_lowercase) + [a + b for a in string.ascii_lowercase for b in string.ascii_lowercase]
print(f"Sweeping {len(prefixes)} prefixes...")

def http_get(url, timeout=15):
    req = urllib.request.Request(url, headers={"User-Agent": "Medtrixa/1.0"})
    return urllib.request.urlopen(req, timeout=timeout).read()

for i, prefix in enumerate(prefixes):
    params = {
        "terms": prefix,
        "maxList": 500,
        "df": "DISPLAY_NAME",
        "ef": "STRENGTHS_AND_FORMS,RXCUIS,SXDG_RXCUI"
    }
    url = BASE + "?" + urllib.parse.urlencode(params)
    try:
        data = json.loads(http_get(url))
        total, codes, extra, display = data[0], data[1], data[2], data[3]
        sxdg = extra.get("SXDG_RXCUI", codes) if extra else codes
        strengths_forms = extra.get("STRENGTHS_AND_FORMS", []) if extra else []
        for j in range(len(codes)):
            key = sxdg[j] if j < len(sxdg) and sxdg[j] else codes[j]
            if key in results:
                continue
            name = display[j][0] if isinstance(display[j], list) else display[j]
            sf = strengths_forms[j] if j < len(strengths_forms) else []
            results[key] = {"display_name": name, "strengths_forms": sf}
    except Exception as e:
        print(f"  skipped '{prefix}': {str(e)[:80]}")
    if (i + 1) % 50 == 0:
        print(f"Progress: {i+1}/{len(prefixes)} prefixes, {len(results)} unique drugs so far")
        with open("rxterms_raw.json", "w") as f:
            json.dump(results, f)
    time.sleep(0.1)

with open("rxterms_raw.json", "w") as f:
    json.dump(results, f)
print(f"Done. {len(results)} unique drugs collected -> rxterms_raw.json")
