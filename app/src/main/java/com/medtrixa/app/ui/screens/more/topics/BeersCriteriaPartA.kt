package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val beersSectionsA: List<TopicSection> = listOf(
    TopicSection("About the Beers Criteria", listOf(
        TopicBlock.Text("The AGS Beers Criteria list medicines that are potentially inappropriate for adults aged 65 and older. This summary follows the 2023 update. Avoid means avoid except under unusual circumstances, such as when a safer option has failed. It is not an absolute contraindication unless the product label says so."),
        TopicBlock.Bullets(listOf(
            "Table 1: grading key. Evidence quality is high, moderate or low. Strength is strong when harms clearly outweigh benefits, and weak when they may not.",
            "Table 2: avoid in most older adults. See Sedatives and Anticholinergics, Cardiovascular and Antithrombotics, Endocrine, and Pain, Gut, Urinary and Other.",
            "Table 3: avoid in certain diseases or syndromes. See Diseases and Syndromes.",
            "Table 4: use with caution. See Use with Caution.",
            "Table 5: drug interactions to avoid. See Drug Interactions to Avoid.",
            "Table 6: avoid or dose-reduce by kidney function. See Kidney Function.",
            "Table 7: strong anticholinergic drugs. See Strong Anticholinergics.",
            "Tables 8 to 11 (drugs removed for low use, additions, changes, principles of use) are in the original paper only.",
            "The criteria apply in ambulatory, acute and institutional care, but not in hospice or end-of-life care."
        )),
        TopicBlock.Callout(
            "Source",
            "Summarised from the American Geriatrics Society 2023 updated Beers Criteria (J Am Geriatr Soc 2023;71:2052-2081). Rationales, evidence ratings and full wording are in the original publication.",
            CalloutKind.INFO
        )
    ), TopicAccent.BLUE),
    TopicSection("Sedatives and Anticholinergics", listOf(
        TopicBlock.Text("Drugs that cloud thinking, slow reactions or block acetylcholine. They raise the risk of delirium, falls and fractures. Consider total anticholinergic burden at each medication review."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("H1 blockers", "First-generation antihistamines", "Avoid.\n> Examples: brompheniramine, chlorpheniramine, cyproheptadine, dimenhydrinate, diphenhydramine (oral), doxylamine, hydroxyzine, meclizine, promethazine, triprolidine\n> Risk: confusion, dry mouth, constipation, and tolerance when used as a hypnotic", "Diphenhydramine can be appropriate for acute treatment of a severe allergic reaction."),
            AbbrevRow("Benzos", "Benzodiazepines", "Avoid.\n> Examples: alprazolam, chlordiazepoxide (alone or with amitriptyline or clidinium), clobazam, clonazepam, clorazepate, diazepam, estazolam, lorazepam, midazolam, oxazepam, temazepam, triazolam\n> Risk: abuse, misuse, addiction, delirium, cognitive impairment, falls, fractures and crashes\n> With opioids: profound sedation, respiratory depression, coma and death", "May be appropriate for seizure disorders, REM sleep behavior disorder, benzodiazepine or ethanol withdrawal, severe generalized anxiety disorder and periprocedural anesthesia. Shorter-acting agents are not safer."),
            AbbrevRow("Z-drugs", "Non-benzodiazepine hypnotics", "Avoid.\n> Examples: eszopiclone, zaleplon, zolpidem\n> Risk: delirium, falls, fractures, crashes and hospital visits, with minimal gain in sleep latency or duration"),
            AbbrevRow("Barbiturates", "Barbiturates", "Avoid.\n> Examples: butalbital, phenobarbital, primidone\n> Risk: physical dependence, tolerance to sleep benefit, overdose at low doses"),
            AbbrevRow("Meprobamate", "Anxiolytic sedative", "Avoid.\n> Risk: high rate of physical dependence, and very sedating"),
            AbbrevRow("TCAs", "Strongly anticholinergic antidepressants", "Avoid, alone or in combination.\n> Examples: amitriptyline, amoxapine, clomipramine, desipramine, doxepin above 6 mg/day, imipramine, nortriptyline, paroxetine\n> Risk: sedation, orthostatic hypotension, anticholinergic effects", "Low-dose doxepin (6 mg/day or less) has a safety profile comparable to placebo."),
            AbbrevRow("Antipsych.", "Antipsychotics, first and second generation", "Avoid, except for FDA-approved indications.\n> Examples of indications: schizophrenia, bipolar disorder, Parkinson disease psychosis, adjunctive treatment of major depression, short-term antiemetic use\n> Dementia or delirium behaviors: avoid unless non-drug options failed and/or the person threatens substantial harm to self or others\n> If used, attempt periodic deprescribing and use the lowest effective dose\n> Risk: stroke, faster cognitive decline and higher mortality in dementia", "Includes aripiprazole, haloperidol, olanzapine, quetiapine, risperidone and others."),
            AbbrevRow("Antispasm.", "GI antispasmodics", "Avoid.\n> Examples: atropine (not ophthalmic), clidinium-chlordiazepoxide, dicyclomine, hyoscyamine, scopolamine\n> Risk: highly anticholinergic, with uncertain effectiveness"),
            AbbrevRow("Relaxants", "Skeletal muscle relaxants", "Avoid.\n> Examples: carisoprodol, chlorzoxazone, cyclobenzaprine, metaxalone, methocarbamol, orphenadrine\n> Risk: anticholinergic effects, sedation and fractures, with questionable effectiveness at tolerated doses", "Does not apply to baclofen and tizanidine used for spasticity, though they can also cause substantial adverse effects."),
            AbbrevRow("Benztropine", "Anti-parkinson anticholinergics", "Avoid.\n> Examples: benztropine (oral), trihexyphenidyl\n> Not recommended to prevent or treat extrapyramidal symptoms from antipsychotics")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("Diseases and Syndromes", listOf(
        TopicBlock.Text("A medicine that is acceptable in general can still worsen a specific condition. Check the patient's history before prescribing. These lists are not comprehensive."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Heart failure", "Heart failure", "Avoid:\n> Cilostazol\n> Dextromethorphan-quinidine\nAvoid in reduced ejection fraction:\n> Diltiazem and verapamil\nUse with caution if asymptomatic, avoid if symptomatic:\n> Dronedarone\n> NSAIDs and COX-2 inhibitors\n> Thiazolidinediones (pioglitazone)"),
            AbbrevRow("Syncope", "Syncope", "Avoid:\n> Chlorpromazine and olanzapine\n> Cholinesterase inhibitors: donepezil, galantamine, rivastigmine\n> Non-selective peripheral alpha-1 blockers: doxazosin, prazosin, terazosin\n> Tertiary TCAs: amitriptyline, clomipramine, doxepin, imipramine"),
            AbbrevRow("Delirium", "Delirium or high risk of delirium", "Avoid:\n> Anticholinergics (Table 7)\n> Antipsychotics\n> Benzodiazepines and Z-drugs\n> H2 blockers: cimetidine, famotidine, nizatidine\n> Opioids (added in 2023)\n> Corticosteroids, oral or parenteral", "If a corticosteroid is needed, use the lowest dose for the shortest time and watch for delirium. Opioids: use multimodal, non-drug pain strategies to minimize use."),
            AbbrevRow("Dementia", "Dementia or cognitive impairment", "Avoid:\n> Anticholinergics (Table 7)\n> Benzodiazepines and Z-drugs\n> Antipsychotics, chronic or persistent as-needed use", "Antipsychotics may be needed for schizophrenia, bipolar disorder and similar conditions, at the lowest dose for the shortest time."),
            AbbrevRow("Falls", "History of falls or fractures", "Avoid unless safer options are not available:\n> Anticholinergics\n> Antidepressants: SSRIs, SNRIs, TCAs\n> Antipsychotics\n> Benzodiazepines and Z-drugs\n> Antiepileptics: avoid except for seizures and mood disorders\n> Opioids: avoid except for severe acute pain", "If one must be used, reduce other CNS-active drugs that raise fall risk."),
            AbbrevRow("Parkinson", "Parkinson disease", "Avoid:\n> Metoclopramide, prochlorperazine and promethazine\n> Antipsychotics, except clozapine, pimavanserin and quetiapine"),
            AbbrevRow("Ulcer", "History of gastric or duodenal ulcer", "Avoid aspirin and non-COX-2-selective NSAIDs unless other options are not effective and the patient can take a gastroprotective agent (proton-pump inhibitor or misoprostol)."),
            AbbrevRow("Incontinence", "Urinary incontinence in women", "Avoid:\n> Non-selective peripheral alpha-1 blockers: doxazosin, prazosin, terazosin\n> Oral and transdermal estrogen (vaginal estrogen is excluded)"),
            AbbrevRow("LUTS/BPH", "Lower urinary tract symptoms or BPH in men", "Avoid strongly anticholinergic drugs, except antimuscarinics used for urinary incontinence.\n> Risk: reduced urinary flow and urinary retention")
        ))
    ), TopicAccent.AMBER)
)
