package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val AcidBaseTopic = MoreTopic(
    id = "acid_base_abg",
    title = "Acid-Base and ABG Interpretation",
    subtitle = "A stepwise approach with compensation rules",
    badge = "ABG",
    accent = TopicAccent.BLUE,
    category = "Laboratory",
    keywords = listOf("abg", "arterial blood gas", "acid base", "acidosis", "alkalosis", "compensation", "winter formula", "anion gap", "delta ratio", "a-a gradient", "pao2", "paco2", "bicarbonate", "ph", "respiratory acidosis", "metabolic acidosis"),
    sections = acidBaseA + acidBaseB,
    references = listOf(
        "Berend K, de Vries APJ, Gans ROB. Physiological approach to assessment of acid-base disturbances. N Engl J Med. 2014;371:1434-1445.",
        "Kraut JA, Madias NE. Metabolic acidosis: pathophysiology, diagnosis and management. Nat Rev Nephrol. 2010;6:274-285.",
        "Adrogue HJ, Madias NE. Management of life-threatening acid-base disorders. N Engl J Med. 1998;338:26-34 and 107-111.",
        "Albert MS, Dell RB, Winters RW. Quantitative displacement of acid-base equilibrium in metabolic acidosis. Ann Intern Med. 1967;66:312-322.",
        "ARDS Definition Task Force. Acute respiratory distress syndrome: the Berlin definition. JAMA. 2012;307:2526-2533."
    )
)
