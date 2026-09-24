#!/usr/bin/env python3
"""Patches MainActivity.kt:
  - "assess" block's onBackClick -> "home" instead of "tools"
  - "calculators" block's onBackClick -> "home" instead of "tools"

Usage: python3 patch_assess_calc_back.py [path/to/MainActivity.kt]
Defaults to app/src/main/java/com/medtrixa/app/MainActivity.kt
Makes a .bak2 copy before writing.
"""
import sys

path = sys.argv[1] if len(sys.argv) > 1 else \
    "app/src/main/java/com/medtrixa/app/MainActivity.kt"

with open(path, encoding="utf-8") as f:
    content = f.read()

original = content

# --- Patch 1: Assess back button -> home ---
old1 = (
    '"assess" -> {\n'
    '            AssessScreen(\n'
    '                onBackClick = { currentScreen = "tools" },'
)
new1 = (
    '"assess" -> {\n'
    '            AssessScreen(\n'
    '                onBackClick = { currentScreen = "home" },'
)
count1 = content.count(old1)
content = content.replace(old1, new1)

# --- Patch 2: Calculators back button -> home ---
old2 = (
    '"calculators" -> {\n'
    '            CalculatorLibraryScreen(\n'
    '                repository = calculatorRepository,\n'
    '                onBackClick = { currentScreen = "tools" },'
)
new2 = (
    '"calculators" -> {\n'
    '            CalculatorLibraryScreen(\n'
    '                repository = calculatorRepository,\n'
    '                onBackClick = { currentScreen = "home" },'
)
count2 = content.count(old2)
content = content.replace(old2, new2)

print("Patch 1 (assess back -> home): found", count1, "match(es)")
print("Patch 2 (calculators back -> home): found", count2, "match(es)")

if count1 != 1 or count2 != 1:
    print("\nOne or both patterns did not match exactly once - no changes written.")
    print("Paste the relevant block(s) here and I'll adjust the script.")
    sys.exit(1)

with open(path + ".bak2", "w", encoding="utf-8") as f:
    f.write(original)

with open(path, "w", encoding="utf-8") as f:
    f.write(content)

print("\nBoth patches applied successfully.")
print("Backup saved to:", path + ".bak2")
