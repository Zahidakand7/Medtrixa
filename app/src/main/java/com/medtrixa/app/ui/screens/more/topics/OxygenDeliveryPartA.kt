package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

internal val o2SectionsA: List<TopicSection> = listOf(
    TopicSection("About Oxygen Delivery", listOf(
        TopicBlock.Text("Oxygen devices are either low-flow (variable FiO₂) or fixed-performance and high-flow (more predictable FiO₂). Low-flow devices deliver less than the patient's inspiratory flow, so room air dilutes the oxygen and the real FiO₂ depends on the breathing pattern."),
        TopicBlock.Bullets(listOf(
            "FiO₂ is the fraction of inspired oxygen. Room air is 0.21 (21%).",
            "Low-flow, variable FiO₂: nasal cannula, simple mask, partial rebreather, non-rebreather mask.",
            "Fixed performance: Venturi mask and air-entrainment systems.",
            "High-flow: heated humidified nasal cannula, with flow and FiO₂ set separately.",
            "Titrate to a target SpO₂ for the patient, not to a fixed device.",
            "Ranges are approximate. Check the product label and local protocol."
        )),
        TopicBlock.Callout(
            "Source",
            "Ranges are drawn from the British Thoracic Society emergency oxygen guideline and standard respiratory care device references. Delivered FiO₂ varies with respiratory rate, tidal volume, mask fit and mouth breathing.",
            CalloutKind.INFO
        )
    ), TopicAccent.BLUE),
    TopicSection("Low-Flow Devices", listOf(
        TopicBlock.Text("Simple devices for patients who need mild to moderate oxygen and are not at risk from an unpredictable FiO₂."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("Nasal cannula", "Nasal prongs", "1 - 6 L/min, FiO₂ about 24 - 44%.\n> Start at 1 - 2 L/min and titrate to the SpO₂ target\n> Estimate FiO₂ as 21% plus 4% for every 1 L/min\n> Humidify if flow is 4 L/min or more and the nose becomes dry or sore", "Above 6 L/min gives little extra FiO₂ and dries the nose. Use a mask or high-flow instead."),
            AbbrevRow("Simple mask", "Simple face mask", "5 - 10 L/min, FiO₂ about 35 - 60%.\n> Do not run below 5 L/min, because exhaled CO₂ can be rebreathed\n> Suited to short-term use while titrating\n> Not suitable when a precise FiO₂ is needed", "Use a nasal cannula if only a little oxygen is needed."),
            AbbrevRow("Partial rebreather", "Mask with reservoir bag", "About 6 - 10 L/min, FiO₂ about 40 - 70% (product-specific).\n> The reservoir bag must stay inflated through inspiration\n> Some exhaled gas is rebreathed into the bag", "Many hospitals no longer stock it, because a simple mask and non-rebreather cover the range."),
            AbbrevRow("NRB", "Non-rebreather mask", "10 - 15 L/min, FiO₂ about 60 - 80%, and up to 90% or more with a tight seal at 15 L/min.\n> One-way valves stop rebreathing\n> Keep the reservoir bag at least partly inflated, and raise the flow if it collapses\n> Use for acute severe hypoxemia, then step down once stable", "Not suited to long-term use, and the face seal matters a great deal.")
        ))
    ), TopicAccent.TEAL),
    TopicSection("Nasal Cannula FiO₂ by Flow", listOf(
        TopicBlock.Text("The rule of 4s: FiO₂ is about 21% plus 4% for every 1 L/min. These are estimates for an adult with normal breathing and a closed mouth."),
        TopicBlock.Abbreviations(listOf(
            AbbrevRow("1 L/min", "FiO₂ about 24%", "Mild hypoxemia, or a low starting point."),
            AbbrevRow("2 L/min", "FiO₂ about 28%", "Common starting flow, including for patients at risk of CO₂ retention."),
            AbbrevRow("3 L/min", "FiO₂ about 32%", "Titrate to the SpO₂ target."),
            AbbrevRow("4 L/min", "FiO₂ about 36%", "Consider humidification from here."),
            AbbrevRow("5 L/min", "FiO₂ about 40%", "If SpO₂ is still below target, change to a mask."),
            AbbrevRow("6 L/min", "FiO₂ about 44%", "Usual maximum for a standard cannula.")
        ))
    ), TopicAccent.GREEN)
)
