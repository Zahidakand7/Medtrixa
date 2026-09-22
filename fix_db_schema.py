import sqlite3

db_path = "app/src/main/assets/database/drugs.db"
conn = sqlite3.connect(db_path)
cur = conn.cursor()

print("Creating corrected table...")
cur.execute("""
CREATE TABLE drugs_new (
    id TEXT NOT NULL PRIMARY KEY,
    genericName TEXT NOT NULL,
    brandName TEXT NOT NULL,
    drugClass TEXT NOT NULL,
    formulation TEXT NOT NULL,
    strength TEXT NOT NULL,
    route TEXT NOT NULL,
    manufacturer TEXT NOT NULL
)
""")

print("Copying data (filling any nulls with empty string)...")
cur.execute("""
INSERT OR IGNORE INTO drugs_new (id, genericName, brandName, drugClass, formulation, strength, route, manufacturer)
SELECT
    COALESCE(id, ''),
    COALESCE(genericName, ''),
    COALESCE(brandName, ''),
    COALESCE(drugClass, ''),
    COALESCE(formulation, ''),
    COALESCE(strength, ''),
    COALESCE(route, ''),
    COALESCE(manufacturer, '')
FROM drugs
WHERE id IS NOT NULL AND id != ''
""")

count = cur.execute("SELECT COUNT(*) FROM drugs_new").fetchone()[0]
print(f"Migrated {count} records.")

cur.execute("DROP TABLE drugs")
cur.execute("ALTER TABLE drugs_new RENAME TO drugs")
conn.commit()
conn.execute("VACUUM")
conn.close()
print("Done. Schema fixed in place.")
