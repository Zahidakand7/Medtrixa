package com.medtrixa.app.ui.screens.more

import com.medtrixa.app.ui.screens.more.topics.PrescriptionTerminologyTopic
import com.medtrixa.app.ui.screens.more.topics.ElectrolyteTopic
import com.medtrixa.app.ui.screens.more.topics.AntimicrobialTopic
import com.medtrixa.app.ui.screens.more.topics.AnticoagTopic
import com.medtrixa.app.ui.screens.more.topics.CypTopic
import com.medtrixa.app.ui.screens.more.topics.AcidBaseTopic
import com.medtrixa.app.ui.screens.more.topics.VitalSignsTopic
import com.medtrixa.app.ui.screens.more.topics.VitalSignsTopic
import com.medtrixa.app.ui.screens.more.topics.ConversionTablesTopic
import com.medtrixa.app.ui.screens.more.topics.HighAlertMedicationsTopic
import com.medtrixa.app.ui.screens.more.topics.LabReferenceRangesTopic
import com.medtrixa.app.ui.screens.more.topics.MedicalTermsTopic
import com.medtrixa.app.ui.screens.more.topics.EmergencyDrugDosesTopic
import com.medtrixa.app.ui.screens.more.topics.OxygenDeliveryTopic
import com.medtrixa.app.ui.screens.more.topics.ImmunizationSchedulesTopic
import com.medtrixa.app.ui.screens.more.topics.BeersCriteriaTopic

object MoreTopicRegistry {
    // Add one line per new topic
    val all: List<MoreTopic> = listOf(
        PrescriptionTerminologyTopic,
        LabReferenceRangesTopic,
        MedicalTermsTopic,
        EmergencyDrugDosesTopic,
        OxygenDeliveryTopic,
        ImmunizationSchedulesTopic,
        BeersCriteriaTopic,
        HighAlertMedicationsTopic,
        ConversionTablesTopic,
        VitalSignsTopic,
        AcidBaseTopic,
        CypTopic,
        AnticoagTopic,
        AntimicrobialTopic,
        ElectrolyteTopic
    )

    fun byId(id: String): MoreTopic? = all.firstOrNull { it.id == id }
}
