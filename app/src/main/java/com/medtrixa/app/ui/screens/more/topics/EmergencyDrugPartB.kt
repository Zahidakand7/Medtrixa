package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val edSectionsB: List<TopicSection> = listOf(
    TopicSection("Bradycardia With a Pulse", listOf(
        TopicBlock.Text("Treat symptomatic bradycardia: heart rate usually below 50/min, with hypotension, altered mental status, ischemic chest pain, acute heart failure or shock. Look for causes such as ischemia, beta blockers, calcium channel blockers, digoxin, hypoxia and hyperkalemia."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Atropine", "First-line drug", "1 mg IV bolus, repeated every 3 - 5 minutes.\n> Maximum total dose 3 mg\n> Less likely to work in Mobitz II or third-degree block with a wide QRS\n> Do not delay pacing in high-grade block"),
            AbbrevRow("Dopamine", "Infusion if atropine fails", "5 - 20 mcg/kg/min IV infusion.\n> Titrate to response\n> Taper slowly"),
            AbbrevRow("Epinephrine", "Infusion if atropine fails", "2 - 10 mcg/min IV infusion.\n> Titrate to response"),
            AbbrevRow("Pacing", "Transcutaneous pacing", "Use if atropine is ineffective, or at once for high-grade block with instability.\n> Dopamine or epinephrine infusion can be used while pacing is set up\n> Transvenous pacing with expert consultation if still refractory")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Tachycardia With a Pulse", listOf(
        TopicBlock.Text("Unstable (hypotension, altered mental status, signs of shock, ischemic chest pain, acute heart failure): synchronized cardioversion. Stable: identify whether the QRS is narrow or wide and the rhythm regular or irregular, then use drugs."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Cardioversion", "Synchronized shock", "Use the device manufacturer's recommended energy.\n> If it is unknown, use the maximum energy setting\n> Sedate a conscious patient if time allows"),
            AbbrevRow("Adenosine", "Regular narrow-complex SVT", "First dose 6 mg rapid IV push, followed at once by a saline flush.\n> Second dose 12 mg if needed\n> Use a proximal vein, such as the antecubital\n> Can be tried for regular, monomorphic wide-complex tachycardia\n> Do not give for unstable, irregularly irregular or polymorphic wide-complex tachycardia", "Start with 3 mg via a central line, in heart transplant recipients, or with dipyridamole or carbamazepine. The 3 mg dose follows common ACLS practice."),
            AbbrevRow("Procainamide", "Stable wide-QRS tachycardia", "20 - 50 mg/min IV until one of these:\n> The arrhythmia is suppressed\n> Hypotension occurs\n> The QRS widens by more than 50%\n> A total of 17 mg/kg has been given\nMaintenance infusion 1 - 4 mg/min.\n> Avoid with prolonged QT or heart failure"),
            AbbrevRow("Amiodarone", "Stable wide-QRS tachycardia", "First dose 150 mg IV over 10 minutes.\n> Repeat as needed if VT recurs\n> Then 1 mg/min for the first 6 hours"),
            AbbrevRow("Sotalol", "Option for stable wide-QRS tachycardia", "AHA 2025 lists it as an option. Avoid with prolonged QT.\n> Dose per the product label and local protocol")
        ))
    ), TopicAccent.BLUE),
    TopicSection("AF and Flutter: Rate Control", listOf(
        TopicBlock.Text("For stable atrial fibrillation or flutter with a rapid ventricular response. Use cardioversion if unstable."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Diltiazem", "Calcium channel blocker", "0.25 mg/kg IV over 2 minutes.\n> Infusion 5 - 10 mg/h\n> Avoid in hypotension, heart failure, cardiomyopathy and acute coronary syndromes"),
            AbbrevRow("Metoprolol", "Beta blocker", "2.5 - 5 mg IV over 2 minutes.\n> Up to 3 doses\n> Avoid in decompensated heart failure"),
            AbbrevRow("Esmolol", "Short-acting beta blocker", "500 mcg/kg IV over 1 minute.\n> Infusion 50 - 300 mcg/kg/min\n> Short duration of action\n> Avoid in decompensated heart failure"),
            AbbrevRow("Amiodarone", "Rate control in critical illness", "300 mg IV over 1 hour.\n> Then 10 - 50 mg/h over 24 hours\n> Useful in critically ill patients without pre-excitation", "Several dosing schemes exist."),
            AbbrevRow("Pre-excited AF", "AF or flutter with an accessory pathway", "Avoid digoxin, non-dihydropyridine calcium channel blockers, beta blockers and IV amiodarone.\n> Use cardioversion, or a drug for the accessory pathway such as procainamide")
        ))
    ), TopicAccent.GREEN)
)
