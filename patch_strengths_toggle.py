#!/usr/bin/env python3
"""
Make the "+N more" chip under STRENGTHS expand/collapse (the brand chips already do).
Changes ONLY DrugDetailScreen.kt, in 4 places; nothing else is touched.

  python patch_strengths_toggle.py path/to/DrugDetailScreen.kt
"""
import sys

if len(sys.argv) != 2:
    sys.exit(__doc__)
path = sys.argv[1]
s = open(path, encoding="utf-8").read()

edits = [
    # 1. state, next to the brand toggle state
    ("    var showAllBrands by remember { mutableStateOf(false) }",
     "    var showAllBrands by remember { mutableStateOf(false) }\n"
     "    var showAllStrengths by remember { mutableStateOf(false) }"),
    # 2. DrugHeroCard parameters
    ("    onToggleBrands: () -> Unit\n) {",
     "    onToggleBrands: () -> Unit,\n    showAllStrengths: Boolean,\n    onToggleStrengths: () -> Unit\n) {"),
    # 3. strength chips + the chip itself becomes clickable
    ("strengths.take(10).forEach { DrugHeroChip(it, filled = true) }",
     "(if (showAllStrengths) strengths else strengths.take(10)).forEach { DrugHeroChip(it, filled = true) }"),
    ('if (strengths.size > 10) DrugHeroChip("+${strengths.size - 10} more", filled = false)',
     'if (strengths.size > 10) DrugHeroChip(\n'
     '                    if (showAllStrengths) "Show less" else "+${strengths.size - 10} more",\n'
     '                    filled = false,\n'
     '                    onClick = onToggleStrengths\n'
     '                )'),
    # 4. call site
    ("onToggleBrands = { showAllBrands = !showAllBrands }",
     "onToggleBrands = { showAllBrands = !showAllBrands },\n"
     "                            showAllStrengths = showAllStrengths,\n"
     "                            onToggleStrengths = { showAllStrengths = !showAllStrengths }"),
]

for old, new in edits:
    n = s.count(old)
    if n != 1:
        sys.exit(f"STOPPED, file not changed: expected 1 match, found {n} for:\n{old}")
    s = s.replace(old, new, 1)

open(path, "w", encoding="utf-8").write(s)
print("Patched. Rebuild the app and tap '+N more' under STRENGTHS.")
