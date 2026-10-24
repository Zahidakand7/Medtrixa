package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val BeersCriteriaTopic = MoreTopic(
    id = "beers_criteria",
    title = "Geriatric Prescribing",
    subtitle = "AGS Beers Criteria highlights for adults 65 and older",
    badge = "AGS",
    accent = TopicAccent.VIOLET,
    category = "Medications",
    keywords = listOf("beers", "beers criteria", "geriatric", "elderly", "older adults", "polypharmacy", "deprescribing", "anticholinergic", "benzodiazepine", "falls", "delirium", "dementia", "renal dosing", "pim", "inappropriate medication"),
    sections = beersSectionsA + beersSectionsB + beersSectionsC,
    references = listOf(
        "2023 American Geriatrics Society Beers Criteria Update Expert Panel. American Geriatrics Society 2023 updated AGS Beers Criteria for potentially inappropriate medication use in older adults. J Am Geriatr Soc. 2023;71(7):2052-2081.",
        "O'Mahony D, et al. STOPP/START criteria for potentially inappropriate prescribing in older people: version 3. Eur Geriatr Med. 2023;14:625-632."
    )
)
