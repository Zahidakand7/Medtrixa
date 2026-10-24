package com.medtrixa.app.ui.screens.more.topics

import com.medtrixa.app.ui.screens.more.*

val OxygenDeliveryTopic = MoreTopic(
    id = "oxygen_delivery",
    title = "Oxygen Delivery Devices",
    subtitle = "Flow rates and FiO₂ for common devices",
    badge = "O₂",
    accent = TopicAccent.BLUE,
    category = "Emergency & Assessment",
    keywords = listOf("oxygen", "o2", "fio2", "nasal cannula", "simple mask", "non-rebreather", "nrb", "venturi", "hfnc", "high flow", "cpap", "bipap", "bvm", "bag valve mask", "flow rate", "spo2", "target saturation", "copd", "hypercapnia", "tracheostomy", "cylinder"),
    sections = o2SectionsA + o2SectionsB + o2SectionsC,
    references = listOf(
        "O'Driscoll BR, et al. BTS Guideline for oxygen use in adults in healthcare and emergency settings. Thorax. 2017;72(Suppl 1):ii1-ii90.",
        "Kallstrom TJ. AARC Clinical Practice Guideline: Oxygen Therapy for Adults in the Acute Care Facility. Respir Care. 2002;47(6):717-720.",
        "Roca O, et al. An index combining respiratory rate and oxygenation to predict outcome of nasal high-flow therapy. Am J Respir Crit Care Med. 2019;199(11):1368-1376.",
        "ARDS Definition Task Force. Acute respiratory distress syndrome: the Berlin Definition. JAMA. 2012;307(23):2526-2533."
    )
)
