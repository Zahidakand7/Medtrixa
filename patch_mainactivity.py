#!/usr/bin/env python3
"""Patches MainActivity.kt:
  1. DrugToolsScreen's onBackClick -> "home" instead of "tools"
  2. The "tools" -> ToolsScreen(...) block replaced with ComingSoonScreen(...)

Usage: python3 patch_mainactivity.py [path/to/MainActivity.kt]
Defaults to app/src/main/java/com/medtrixa/app/MainActivity.kt
Makes a .bak copy before writing.
"""
import sys

path = sys.argv[1] if len(sys.argv) > 1 else \
    "app/src/main/java/com/medtrixa/app/MainActivity.kt"

with open(path, encoding="utf-8") as f:
    content = f.read()

original = content

# --- Patch 1: Drug Tools back button -> home ---
old1 = (
    '"drug_tools" -> {\n'
    '            DrugToolsScreen(\n'
    '                onBackClick = { currentScreen = "tools" },'
)
new1 = (
    '"drug_tools" -> {\n'
    '            DrugToolsScreen(\n'
    '                onBackClick = { currentScreen = "home" },'
)
count1 = content.count(old1)
content = content.replace(old1, new1)

# --- Patch 2: Tools screen -> ComingSoonScreen ---
old2 = (
    '"tools" -> {\n'
    '            ToolsScreen(\n'
    '                onHomeClick = { currentScreen = "home" },\n'
    '                onMoreClick = { currentScreen = "more" },\n'
    '                onCalculateClick = { currentScreen = "calculators" },\n'
    '                onAssessClick = { currentScreen = "assess" },\n'
    '                onDrugToolsClick = { currentScreen = "drug_tools" },\n'
    '                onReferenceClick = { currentScreen = "reference" }\n'
    '            )\n'
    '        }'
)
new2 = (
    '"tools" -> {\n'
    '            ComingSoonScreen(\n'
    '                title = "Tools",\n'
    '                onBackClick = { currentScreen = "home" }\n'
    '            )\n'
    '        }'
)
count2 = content.count(old2)
content = content.replace(old2, new2)

print("Patch 1 (drug_tools back -> home): found", count1, "match(es)")
print("Patch 2 (tools -> ComingSoonScreen): found", count2, "match(es)")

if count1 != 1 or count2 != 1:
    print("\nOne or both patterns did not match exactly once - no changes written.")
    print("This can happen if the file has already been edited, or has slightly")
    print("different spacing than expected. Paste the file content here and I'll")
    print("adjust the script.")
    sys.exit(1)

with open(path + ".bak", "w", encoding="utf-8") as f:
    f.write(original)

with open(path, "w", encoding="utf-8") as f:
    f.write(content)

print("\nBoth patches applied successfully.")
print("Backup saved to:", path + ".bak")
