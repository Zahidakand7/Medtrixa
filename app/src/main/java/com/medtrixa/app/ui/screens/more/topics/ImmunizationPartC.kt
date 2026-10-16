package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val immunSectionsC: List<TopicSection> = listOf(
    TopicSection("Adults 19 and Older: Routine", listOf(
        TopicBlock.Text("Vaccines recommended for most adults, with age and risk cut-offs. COVID-19 products and doses change each season."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Flu", "Influenza vaccine", "1 dose every year.\n> Age 65 and older: high-dose (HD-IIV3), recombinant (RIV3) or adjuvanted (aIIV3) preferred. If none is available, use any age-appropriate vaccine\n> Solid organ transplant, age 19 - 64, on immunosuppressants: HD-IIV3 or aIIV3 are acceptable\n> LAIV3 is an option at age 19 - 49", "Egg allergy: any age-appropriate vaccine. Caregivers of severely immunosuppressed people in a protected environment should not receive LAIV3."),
            AbbrevRow("COVID-19", "COVID-19 vaccine", "Follow the current CDC guidance for the season.\n> CDC 2026 - 2027 guidance recommends a vaccine for all adults 18 and older\n> The 2025 adult schedule: age 19 - 64, 1 dose of the updated vaccine; age 65 and older, a 2nd dose 6 months later (minimum 2 months)\n> Moderately or severely immunocompromised: a multi-dose initial series with one manufacturer, then extra doses by SCDM at least 2 months apart", "Check the CDC COVID-19 page for the current product and dose."),
            AbbrevRow("Tdap/Td", "Tetanus, diphtheria, pertussis", "Completed primary series: Td or Tdap every 10 years, with 1 Tdap dose if none was given at age 10 or older.\nIncomplete primary series:\n> 1 dose Tdap, then Td or Tdap at least 4 weeks later, then Td or Tdap 6 - 12 months later\n> Then Td or Tdap every 10 years\nPregnancy: 1 dose of Tdap in every pregnancy, ideally in weeks 27 - 36."),
            AbbrevRow("HPV", "Human papillomavirus vaccine", "Through age 26: 2 or 3 doses, by age at the first dose.\n> First dose at 15 or older: 3 doses at 0, 1 - 2 and 6 months\n> Immunocompromised, including HIV: 3 doses\nAge 27 - 45: SCDM.\n> Pregnancy: delay until after delivery"),
            AbbrevRow("MMR", "Measles, mumps, rubella", "No evidence of immunity: 1 dose.\n> Evidence: born before 1957 (not health care personnel), documented vaccination, or laboratory evidence of immunity or disease\nGive 2 doses at least 4 weeks apart to:\n> Health care personnel born in 1957 or later\n> College and other postsecondary students\n> International travelers\n> Household contacts of immunocompromised people\n> HIV with CD4 15% or more and 200 or more for 6 months\nWomen of childbearing age without rubella immunity: 1 dose.\n> Pregnancy: contraindicated, and give after delivery before discharge", "Also contraindicated in HIV with CD4 below 15% or below 200 cells/mm³, and in severe immunocompromise."),
            AbbrevRow("VAR", "Varicella vaccine", "No evidence of immunity: 2 doses 4 - 8 weeks apart, or 1 dose if 1 was already given.\n> Evidence: US-born before 1980 (not pregnant women or health care personnel), 2 documented doses, provider-verified varicella or zoster, or lab evidence\n> Pregnancy: contraindicated, and give after delivery\n> HIV with CD4 15% or more and 200 or more: consider 2 doses 3 months apart", "Contraindicated in severe immunocompromise."),
            AbbrevRow("Zoster", "Recombinant zoster vaccine (Shingrix)", "Age 50 and older: 2 doses 2 - 6 months apart (minimum 4 weeks), even after past shingles or Zostavax.\n> Immunocompromised, age 19 and older, including HIV: same 2-dose series\n> Pregnancy: consider delaying"),
            AbbrevRow("RSV", "RSV vaccine", "Age 75 and older: 1 dose of Arexvy, Abrysvo or mResvia.\nAge 50 - 74 at increased risk of severe RSV: 1 dose.\n> No repeat dose\n> Best given in late summer or early fall, ideally August - October\nPregnant at 32 weeks 0 days to 36 weeks 6 days, September - January: 1 dose Abrysvo.", "Increased risk includes chronic heart, lung, kidney or liver disease, diabetes with complications or on insulin or SGLT2 inhibitor, BMI 40 or more, immune compromise, nursing home residence and frailty. People can self-attest.")
        ))
    ), TopicAccent.GREEN),
    TopicSection("Adults: Hepatitis A and B", listOf(
        TopicBlock.Text("Hepatitis B is routine through age 59. After that it depends on risk and request."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("HepB", "Hepatitis B vaccine", "Age 19 - 59: complete a series.\n> Heplisav-B: 2 doses at least 4 weeks apart\n> Engerix-B, Recombivax HB or PreHevbrio: 3 doses at 0, 1 and 6 months (minimum 4 weeks, 8 weeks, and 16 weeks from dose 1 to 3)\n> Twinrix: 3 doses at 0, 1 and 6 months, or 4 doses (0, 7 and 21 - 30 days, then 12 months)\nAge 60 and older:\n> With risk factors: should be vaccinated\n> Without risk factors: may be vaccinated\n> Anyone who asks: should be vaccinated\n> With diabetes: SCDM", "PreHevbrio is not recommended in pregnancy."),
            AbbrevRow("HepB dialysis", "Dialysis and immunocompromised", "Dialysis:\n> Recombivax HB dialysis formulation (40 mcg), 3 doses at 0, 1 and 6 months\n> Engerix-B 2 mL per dose, 4 doses at 0, 1, 2 and 6 months\nImmunocompromised, age 20 and older: same dialysis options, Heplisav-B at 0 and 1 months, or PreHevbrio at 0, 1 and 6 months."),
            AbbrevRow("HepB risk", "Risk factors for hepatitis B", "Chronic liver disease, HIV, sexual exposure risk, injection drug use, blood exposure risk, incarceration, travel to endemic areas.\n> Blood exposure includes household contacts of HBsAg-positive people, health care and public safety personnel, dialysis patients and people with diabetes"),
            AbbrevRow("HepA", "Hepatitis A vaccine", "Anyone not fully vaccinated who asks, with no risk factor needed.\nAt risk: chronic liver disease, HIV, men who have sex with men, drug use, homelessness, lab work with the virus, travel to high or intermediate areas, close contact with an international adoptee in the first 60 days, pregnancy with risk, and some group settings.\n> Havrix: 2 doses 6 - 12 months apart\n> Vaqta: 2 doses 6 - 18 months apart\n> Twinrix: 3 doses at 0, 1 and 6 months, or an accelerated 0, 7 and 21 - 30 days plus a 12-month booster for travelers")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Adults: Pneumococcal", listOf(
        TopicBlock.Text("Recommended from age 50, and from 19 with certain conditions. Underlying conditions include alcoholism, chronic heart, liver, lung or kidney disease, smoking, diabetes, cochlear implant, CSF leak, asplenia, immunodeficiency, malignancy, transplant, HIV and sickle cell disease."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("No prior PCV", "Never vaccinated or unknown", "Age 50 and older, or 19 - 49 with a condition: 1 dose PCV15, PCV20 or PCV21.\n> If PCV15 is used, give PPSV23 at least 1 year later\n> Minimum interval 8 weeks for immunocompromising conditions, cochlear implant or CSF leak"),
            AbbrevRow("Prior PCV13", "Only PCV13 so far", "1 dose PCV20 or PCV21 at least 1 year after the PCV13 dose."),
            AbbrevRow("Prior PPSV23", "Only PPSV23 so far", "1 dose PCV15, PCV20 or PCV21 at least 1 year after the PPSV23 dose.\n> If PCV15 is used, no more PPSV23 is needed"),
            AbbrevRow("PCV13 + PPSV23", "Both given", "Age 50 and older, with no PPSV23 at 65 or older: 1 dose PCV20 or PCV21 at least 5 years after the last dose.\nPPSV23 given at 65 or older: SCDM for 1 dose PCV20 or PCV21 at least 5 years after the last dose.\nAge 19 - 49 with immunocompromise, cochlear implant or CSF leak: 1 dose PCV20 or PCV21 at least 5 years after the last dose.\nAge 19 - 49 with other conditions: no more doses until age 50."),
            AbbrevRow("Complete", "PCV20 or PCV21 received", "No additional pneumococcal vaccine is recommended at age 19 and older."),
            AbbrevRow("Pregnancy", "PCV and PPSV23", "No recommendation, because data are limited.", "If PPSV23 is not available for someone who had PCV15, PCV20 or PCV21 can complete the series.")
        ))
    ), TopicAccent.BLUE),
    TopicSection("Adults: Special Situations", listOf(
        TopicBlock.Text("Vaccines for risk groups, pregnancy and immunocompromise."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("MenACWY", "Meningococcal ACWY", "Asplenia, HIV, complement deficiency or inhibitor use:\n> 2-dose primary series of Menveo or MenQuadfi at least 8 weeks apart\n> 1 booster 5 years later, then every 5 years while risk remains\nTravel to hyperendemic or epidemic areas, and microbiologists:\n> 1 dose, 1 booster 5 years later, then every 5 years while risk remains\nFirst-year college students in residential housing (if not vaccinated at 16 or older) and military recruits: 1 dose."),
            AbbrevRow("MenB", "Meningococcal B", "SCDM at age 16 - 23, with 16 - 18 preferred.\n> Bexsero or Trumenba, 2 doses at least 6 months apart\nAsplenia, complement deficiency or inhibitor use, and microbiologists:\n> 3 doses at 0, 1 - 2 and 6 months\n> Booster 1 year after the series, then every 2 - 3 years while risk remains\n> Pregnancy: delay unless risk is high", "Penbraya can replace separate MenACWY and MenB doses given on the same day."),
            AbbrevRow("Hib", "Haemophilus influenzae type b", "Asplenia: 1 dose if not vaccinated before.\n> Elective splenectomy: 1 dose, ideally at least 14 days before\nHSCT: 3 doses 4 weeks apart, starting 6 - 12 months after transplant."),
            AbbrevRow("IPV", "Poliovirus vaccine", "Unvaccinated or incomplete: finish a 3-dose primary series.\n> Completed series and at increased risk of exposure: 1 lifetime booster\n> Adults born and raised in the US can usually assume childhood vaccination"),
            AbbrevRow("Mpox", "Jynneos", "At risk: 2 doses 28 days apart.\n> Not routine for health care personnel"),
            AbbrevRow("Pregnancy", "Vaccines in pregnancy", "Give:\n> Tdap in weeks 27 - 36 of every pregnancy\n> Inactivated influenza vaccine\n> Abrysvo at 32 weeks 0 days to 36 weeks 6 days, September - January\nDo not give during pregnancy:\n> MMR and varicella (give after delivery if not immune)\n> HPV and zoster (delay)\n> MenB (delay unless risk is high)"),
            AbbrevRow("Immuno", "Immunocompromised adults", "MMR and varicella are contraindicated in severe immunocompromise.\n> HIV: contraindicated if CD4 is below 15% or below 200 cells/mm³\nRecommended:\n> Shingrix from age 19\n> Pneumococcal conjugate vaccine\n> Hib after HSCT\n> A multi-dose COVID-19 series", "See Table 8-1 of the CDC General Best Practice Guidelines for details by condition.")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Clinical Pearls", listOf(
        TopicBlock.Bullets(listOf(
            "Check the vaccine record at every visit, and give missing vaccines the same day when possible.",
            "A mild illness is not a reason to delay vaccination. Review the contraindications and precautions in the CDC appendix first.",
            "Vaccines can be given at the same visit at different sites. MenACWY and MenB are given at separate sites where feasible.",
            "Do not restart a series after a long gap. Count valid prior doses and continue.",
            "Ask about pregnancy, immunocompromise, asplenia, chronic disease, travel and occupation, because each one changes the schedule.",
            "Report suspected vaccine-preventable disease to the health department and adverse events to VAERS.",
            "Check which schedule your state, school or payer follows, since the federal and professional society schedules may differ."
        )),
        TopicBlock.Callout(
            "Scope",
            "This is a summary of the US CDC schedules and does not cover other countries. Federal recommendations are under court review, and COVID-19 and influenza products change every season, so confirm current guidance before vaccinating.",
            CalloutKind.INFO
        )
    ), TopicAccent.VIOLET)
)
