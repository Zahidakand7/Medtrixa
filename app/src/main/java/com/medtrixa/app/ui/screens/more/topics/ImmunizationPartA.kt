package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val immunSectionsA: List<TopicSection> = listOf(
    TopicSection("About the Schedules", listOf(
        TopicBlock.Text("A summary of the US CDC and ACIP immunization schedules for children and adolescents (18 years or younger) and adults (19 years or older). Read each entry with its notes. Doses given 4 days or fewer before the minimum age or interval count as valid. Doses given 5 or more days early should be repeated after the minimum interval."),
        TopicBlock.Bullets(listOf(
            "The CDC schedules shown are the 2025 versions in force.",
            "A January 2026 revision of the child schedule was stayed by a federal court on 16 March 2026 and is under appeal. Check for a ruling before relying on this topic.",
            "The AAP, AAFP and ACOG publish their own schedules, which can differ from the CDC's.",
            "SCDM means shared clinical decision-making: the vaccine is given after the clinician and the patient or parent discuss it.",
            "Do not restart a series because the gap between doses was long. Give the next dose.",
            "Live vaccines such as MMR and varicella are contraindicated in pregnancy and in severe immunocompromise."
        )),
        TopicBlock.Callout(
            "Source",
            "Summarised from the CDC Child and Adolescent Immunization Schedule (2025, addendum of 2 July 2025) and the CDC Adult Immunization Schedule (2025, amended 27 April 2026). Contraindications, precautions and product details are in the CDC notes and appendices.",
            CalloutKind.INFO
        )
    ), TopicAccent.BLUE),
    TopicSection("Infants: Birth to 15 Months", listOf(
        TopicBlock.Text("Routine series for infants. Ages are the recommended ages, and catch-up rules are in Catch-up and Timing Rules."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("HepB", "Hepatitis B vaccine", "3 doses at birth, 1 - 2 months and 6 - 18 months.\n> Mother HBsAg-negative, weight 2,000 g or more: dose 1 within 24 hours of birth if medically stable\n> Weight under 2,000 g: dose 1 at 1 month or hospital discharge, whichever is earlier\n> Final dose at 6 - 18 months, and not before 24 weeks\nMother HBsAg-positive:\n> Vaccine and HBIG in separate limbs within 12 hours of birth, at any weight\n> Weight under 2,000 g: 3 more doses from 1 month (4 in total)\n> Final dose at 6 months, and test HBsAg and anti-HBs at 9 - 12 months\nMother HBsAg-unknown:\n> Vaccine within 12 hours of birth, and HBIG as soon as possible, within 7 days, if she is positive", "Use monovalent vaccine for doses before 6 weeks. A 4th dose is allowed when a combination vaccine is used after the birth dose."),
            AbbrevRow("RSV-mAb", "Nirsevimab", "1 dose for infants whose mother did not receive RSV vaccine, or whose status is unknown.\n> Born October - March: within 1 week of birth, ideally in hospital\n> Born April - September: shortly before RSV season\n> Mother vaccinated 14 days or more before delivery: not needed\nAge 8 - 19 months with high risk:\n> 1 dose before the second season for chronic lung disease of prematurity, severe immunocompromise, cystic fibrosis with severe disease, or American Indian or Alaska Native children", "RSV season is October - March in most of the continental US. Either maternal vaccine or infant nirsevimab is recommended."),
            AbbrevRow("RV", "Rotavirus vaccine", "Rotarix: 2 doses at 2 and 4 months.\nRotaTeq: 3 doses at 2, 4 and 6 months.\n> If any dose is RotaTeq or unknown, use the 3-dose series\n> Do not start on or after 15 weeks 0 days\n> Final dose by 8 months 0 days"),
            AbbrevRow("DTaP", "Diphtheria, tetanus, acellular pertussis", "5 doses at 2, 4, 6, 15 - 18 months and 4 - 6 years.\n> Dose 4 may be given from 12 months if 6 months have passed since dose 3\n> Dose 5 is not needed if dose 4 was given at 4 years or older and 6 months or more after dose 3"),
            AbbrevRow("Hib", "Haemophilus influenzae type b", "ActHIB, Hiberix, Pentacel, Vaxelis: 4 doses at 2, 4, 6 and 12 - 15 months.\nPedvaxHIB: 3 doses at 2, 4 and 12 - 15 months.\n> Vaxelis is not for the booster dose\n> American Indian and Alaska Native infants: Vaxelis or PedvaxHIB preferred for the primary series"),
            AbbrevRow("PCV", "Pneumococcal conjugate vaccine", "PCV15 or PCV20: 4 doses at 2, 4, 6 and 12 - 15 months."),
            AbbrevRow("IPV", "Inactivated poliovirus vaccine", "4 doses at 2, 4, 6 - 18 months and 4 - 6 years.\n> The final dose is on or after age 4 and at least 6 months after the previous dose\n> Extra doses before age 4 are fine when a combination vaccine is used"),
            AbbrevRow("Flu", "Influenza vaccine, inactivated", "From 6 months, every year.\n> Age 6 months - 8 years with fewer than 2 prior doses, or unknown history: 2 doses at least 4 weeks apart\n> 2 or more prior doses: 1 dose\n> Age 9 and older: 1 dose", "Egg allergy: any age-appropriate vaccine can be used. LAIV3 is for ages 2 and older."),
            AbbrevRow("COVID-19", "COVID-19 vaccine", "Age 6 months - 17 years, not immunocompromised: SCDM on the 2025 CDC schedule.\n> A child 6 months or older may be vaccinated when the parent requests it\n> Moderately or severely immunocompromised: an age-based multi-dose series is recommended\n> Use the same manufacturer for the whole initial series", "A March 2026 court order stayed the May 2025 federal directive behind the SCDM wording, but the CDC page still shows it. Products and doses change each season, so confirm with CDC or AAP.")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Ages 12 Months to 6 Years", listOf(
        TopicBlock.Text("Second doses and boosters. Doses of DTaP, Hib, PCV and IPV are in the infant section."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("MMR", "Measles, mumps, rubella", "2 doses at 12 - 15 months and 4 - 6 years.\n> MMR or MMRV (up to age 12)\n> Dose 1 at 12 - 47 months: separate MMR and varicella is recommended, and MMRV if the parent prefers\n> MMRV doses must be at least 3 months apart\nTravel:\n> Infants 6 - 11 months: 1 dose before departure, then a 2-dose series from 12 - 15 months\n> Age 12 months or older: 2 doses at least 4 weeks apart before departure"),
            AbbrevRow("VAR", "Varicella vaccine", "2 doses at 12 - 15 months and 4 - 6 years.\n> Dose 2 may be given as early as 3 months after dose 1\n> A dose given 4 weeks or more after dose 1 by mistake still counts"),
            AbbrevRow("HepA", "Hepatitis A vaccine", "2 doses at least 6 months apart, starting at 12 - 23 months.\n> Travel to high or intermediate risk countries: infants 6 - 11 months get 1 dose before departure, then 2 doses from 12 - 23 months\n> Unvaccinated at 12 months or older: dose 1 as soon as travel is planned"),
            AbbrevRow("Boosters", "DTaP and IPV at 4 - 6 years", "DTaP dose 5 and IPV dose 4.\n> Kinrix and Quadracel are licensed from age 4\n> Skip DTaP dose 5 if dose 4 was given at 4 years or older and 6 months after dose 3"),
            AbbrevRow("Flu", "Influenza vaccine", "Every year from 6 months, with 2 doses in the first season for ages 6 months - 8 years if history is incomplete.")
        ))
    ), TopicAccent.GREEN)
)
