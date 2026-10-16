package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val o2SectionsB: List<TopicSection> = listOf(
    TopicSection("Venturi and Fixed-Performance", listOf(
        TopicBlock.Text("Venturi adaptors pull in room air at a set ratio, so the FiO₂ stays fixed at the oxygen flow marked on the adaptor. Colors differ between manufacturers, so read the FiO₂ and flow printed on the adaptor. Do not lower the flow below the marked value, and do not add extra oxygen into the mask."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Venturi 24%", "Lowest setting", "About 2 L/min (2 - 3 L/min).\n> First choice for patients at risk of CO₂ retention"),
            AbbrevRow("Venturi 28%", "Low setting", "About 4 L/min.\n> Also used for patients at risk of CO₂ retention"),
            AbbrevRow("Venturi 31%", "Low to medium", "About 6 L/min."),
            AbbrevRow("Venturi 35%", "Medium", "About 8 L/min."),
            AbbrevRow("Venturi 40%", "Medium to high", "About 10 L/min."),
            AbbrevRow("Venturi 50 - 60%", "Highest settings", "About 12 - 15 L/min.\n> Not all kits include these settings\n> Above 40% a non-rebreather or high-flow is usually used instead"),
            AbbrevRow("Aerosol mask", "Air-entrainment nebulizer", "FiO₂ set on the dial, about 28 - 100%.\n> Delivers humidified oxygen\n> Keep the mist visible during inspiration, and raise the flow if it disappears"),
            AbbrevRow("Trach collar", "Tracheostomy mask or T-piece", "Humidified oxygen by entrainment, with FiO₂ about 28 - 100%.\n> Keep the mist visible\n> Use for tracheostomy patients who are breathing spontaneously")
        ))
    ), TopicAccent.VIOLET),
    TopicSection("High-Flow and Advanced", listOf(
        TopicBlock.Text("For patients who fail low-flow devices, or who need ventilatory support."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("HFNC adult", "Heated humidified high-flow nasal cannula", "Flow up to 60 L/min, and FiO₂ set separately from 21 - 100%.\n> Typical start 30 - 40 L/min, with FiO₂ set to the SpO₂ target\n> Wean FiO₂ first, then flow\n> Gas is warmed and humidified, about 34 - 37 °C", "Gives some PEEP and flushes dead space. A trial mask is not needed to start."),
            AbbrevRow("ROX index", "HFNC success predictor", "ROX = (SpO₂ / FiO₂) divided by respiratory rate.\n> 4.88 or above at 2, 6 or 12 hours suggests success\n> Below 2.85 at 2 hours, or below 3.85 at 12 hours, suggests failure and the need to consider intubation", "A guide only. Use it with the clinical picture."),
            AbbrevRow("CPAP", "Continuous positive airway pressure", "Usually 5 - 10 cm H₂O, with FiO₂ 21 - 100%.\n> Used for cardiogenic pulmonary edema, and some hypoxemic respiratory failure\n> The patient must breathe spontaneously and protect the airway"),
            AbbrevRow("BiPAP", "Bilevel positive airway pressure", "Typical start IPAP 8 - 10 and EPAP 4 - 5 cm H₂O, with FiO₂ 21 - 100%.\n> Used for hypercapnic respiratory failure such as a COPD exacerbation\n> Titrate to tidal volume, respiratory rate and blood gas"),
            AbbrevRow("BVM", "Bag-valve-mask", "10 - 15 L/min with a reservoir, giving FiO₂ about 90 - 100%.\n> Adults: 1 breath every 5 - 6 seconds\n> Infants and children: 1 breath every 2 - 3 seconds\n> Use a good mask seal, and avoid over-ventilation"),
            AbbrevRow("Ventilator", "Invasive mechanical ventilation", "FiO₂ 21 - 100%, titrated to the SpO₂ or PaO₂ target.\n> Wean FiO₂ to 60% or less as soon as it is safe\n> Prolonged high FiO₂ can injure the lungs")
        ))
    ), TopicAccent.AMBER),
    TopicSection("Infants and Children", listOf(
        TopicBlock.Text("Flow limits and sizes differ in infants and children, and unit protocols vary. Use age-appropriate interfaces and follow the local protocol."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Nasal cannula", "Infant and child sizes", "Infants usually up to 2 L/min, and children up to 4 L/min.\n> Above this, consider a mask or high-flow\n> Dry gas at higher flows irritates the nose"),
            AbbrevRow("Masks", "Pediatric simple and NRB", "Simple mask: 5 - 10 L/min, and never below 5 L/min.\nNon-rebreather: 10 - 15 L/min with the bag inflated.\n> Use the correct mask size for a good seal"),
            AbbrevRow("Hood", "Oxygen hood", "FiO₂ 21 - 100%, measured with an oxygen analyzer near the infant's face.\n> Flow must be enough to flush CO₂\n> Used for neonates and young infants"),
            AbbrevRow("Blow-by", "Oxygen held near the face", "Use only for short periods, such as a child who will not tolerate a mask.\n> FiO₂ is low and unreliable\n> Hold the tubing close to the face at 10 L/min or more"),
            AbbrevRow("HFNC child", "Pediatric high-flow", "Flow is weight-based, commonly about 1 - 2 L/kg/min.\n> Follow the unit protocol for the maximum flow\n> Often used for bronchiolitis and other respiratory distress", "Check the device and age limits before using it."),
            AbbrevRow("Preterm", "Premature infants", "Target SpO₂ about 90 - 95% in preterm infants, per unit protocol.\n> Avoid high FiO₂ and high SpO₂, because of retinopathy of prematurity and lung injury\n> Use an air-oxygen blender")
        ))
    ), TopicAccent.ROSE)
)
