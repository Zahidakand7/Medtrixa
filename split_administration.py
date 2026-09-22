import sqlite3
import re

DB_PATH = "app/src/main/assets/database/drugs.db"

conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()

conn.commit()

ADMIN_KEYWORDS = [
    r"\badminister\w*\b", r"\btake\b", r"\btaken\b", r"\bwith food\b",
    r"\bwithout food\b", r"\binjection site\b", r"\binject\w*\b",
    r"\binfus\w*\b", r"\bswallow\w*\b", r"\bcrush\w*\b", r"\bchew\w*\b",
    r"\bapply\b", r"\bapplied\b", r"\brotate\b", r"\bsubcutaneous\w*\b",
    r"\bintravenous\w*\b", r"\bintramuscular\w*\b", r"\bat the same time\b",
    r"\bmorning\b", r"\bevening\b", r"\bbefore meals\b", r"\bafter meals\b",
]
ADMIN_PATTERN = re.compile("|".join(ADMIN_KEYWORDS), re.IGNORECASE)

def split_sentences(text):
    if not text:
        return "", ""
    sentences = re.split(r'(?<=[.!?])\s+', text)
    dose_sentences = []
    admin_sentences = []
    for s in sentences:
        if ADMIN_PATTERN.search(s):
            admin_sentences.append(s)
        else:
            dose_sentences.append(s)
    return " ".join(dose_sentences).strip(), " ".join(admin_sentences).strip()

cur.execute("SELECT genericName, adultDose, childDose FROM drug_labels WHERE fetchStatus = 'ok'")
rows = cur.fetchall()
print(f"Splitting {len(rows)} entries...")

count = 0
for generic, adult_text, child_text in rows:
    adult_dose, adult_admin = split_sentences(adult_text)
    child_dose, child_admin = split_sentences(child_text)
    cur.execute("""
        UPDATE drug_labels
        SET adultDose = ?, adultAdministration = ?, childDose = ?, childAdministration = ?
        WHERE genericName = ?
    """, (adult_dose, adult_admin, child_dose, child_admin, generic))
    count += 1
    if count % 500 == 0:
        conn.commit()
        print(f"Progress: {count}/{len(rows)}")

conn.commit()
conn.close()
print(f"Done. Split {count} entries.")
