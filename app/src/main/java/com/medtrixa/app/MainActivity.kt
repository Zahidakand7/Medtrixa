package com.medtrixa.app

import com.medtrixa.app.ui.screens.HomePage
import com.medtrixa.app.ui.screens.InsulinDosingScreen
import com.medtrixa.app.ui.screens.InsulinDosing2Screen
import com.medtrixa.app.ui.screens.DiabetesScreen
import com.medtrixa.app.ui.screens.StatusEpilepticusScreen
import com.medtrixa.app.ui.screens.DkaScreen
import com.medtrixa.app.ui.screens.more.MoreTopicsScreen
import com.medtrixa.app.ui.screens.more.MoreTopicDetailScreen
import com.medtrixa.app.ui.screens.ClinicalSourcesScreen
import com.medtrixa.app.ui.screens.preloadAntibioticGuidelines
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.medtrixa.app.data.model.MedicalCalculator
import com.medtrixa.app.data.repository.CalculatorRepository
import com.medtrixa.app.ui.screens.CalculatorLibraryScreen
import com.medtrixa.app.ui.screens.CalculatorScreen
import com.medtrixa.app.ui.screens.AssessScreen
import com.medtrixa.app.ui.screens.RiskAssessmentScreen
import com.medtrixa.app.ui.screens.ClinicalScoresScreen
import com.medtrixa.app.ui.screens.ComingSoonScreen
import com.medtrixa.app.ui.screens.DrugToolsScreen
import com.medtrixa.app.ui.screens.DrugSearchScreen
import com.medtrixa.app.ui.screens.DrugDetailScreen
import com.medtrixa.app.ui.screens.drugtools.RenalDoseAdjustmentScreen
import com.medtrixa.app.ui.screens.drugtools.HalfLifeWashoutScreen
import com.medtrixa.app.ui.screens.drugtools.PregnancyLactationScreen
import com.medtrixa.app.ui.screens.drugtools.TherapeuticAlternativesScreen
import com.medtrixa.app.ui.screens.drugtools.InteractionCheckerScreen
import com.medtrixa.app.ui.screens.drugtools.ContraindicationCheckerScreen
import com.medtrixa.app.ui.screens.calculators.WeightBasedDoseScreen
import com.medtrixa.app.ui.screens.calculators.CreatinineClearanceScreen
import com.medtrixa.app.ui.screens.calculators.PediatricDoseScreen
import com.medtrixa.app.ui.screens.calculators.InfusionRateScreen
import com.medtrixa.app.ui.screens.assess.PercScreen
import com.medtrixa.app.ui.screens.assess.Curb65Screen
import com.medtrixa.app.ui.screens.assess.Cha2ds2VascScreen
import com.medtrixa.app.ui.screens.assess.QsofaScreen
import com.medtrixa.app.ui.screens.assess.HasBledScreen
import com.medtrixa.app.ui.screens.assess.WellsDvtScreen
import com.medtrixa.app.ui.screens.assess.WellsPeScreen
import com.medtrixa.app.ui.screens.assess.GcsScreen
import com.medtrixa.app.ui.screens.assess.ApgarScreen
import com.medtrixa.app.ui.screens.assess.HeartScoreScreen
import com.medtrixa.app.ui.screens.assess.Meld30Screen
import com.medtrixa.app.ui.screens.assess.ChildPughScreen
import com.medtrixa.app.ui.screens.assess.CentorScoreScreen
import com.medtrixa.app.ui.screens.AppBottomNavigation
import com.medtrixa.app.ui.screens.ConditionsScreen
import com.medtrixa.app.ui.screens.HomeScreen
import com.medtrixa.app.ui.screens.AiInsightsScreen
import com.medtrixa.app.ui.screens.ProtocolsScreen
import com.medtrixa.app.ui.screens.pulse.PulseScreen
import com.medtrixa.app.ui.screens.CardiacArrestScreen
import com.medtrixa.app.ui.screens.AbgInterpreterScreen
import com.medtrixa.app.ui.screens.AnaphylaxisScreen
import com.medtrixa.app.ui.screens.StrokeScreen
import com.medtrixa.app.ui.screens.NihssScreen
import com.medtrixa.app.ui.screens.ProtocolsHubScreen
import com.medtrixa.app.ui.screens.SbarHandoverScreen
import com.medtrixa.app.ui.screens.SepsisScreen
import com.medtrixa.app.ui.screens.LabInterpreterScreen
import com.medtrixa.app.ui.screens.ReferenceScreen
import com.medtrixa.app.ui.screens.PubMedScreen
import com.medtrixa.app.ui.screens.AntimicrobialReferenceHubScreen
import com.medtrixa.app.ui.screens.BacterialAtlasScreen
import com.medtrixa.app.ui.screens.AntibioticDosingGuidelinesScreen
import com.medtrixa.app.ui.theme.MedtrixaTheme
import com.medtrixa.app.ui.screens.AntibioticStewardshipReferenceScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Forces consistent edge-to-edge on every API level (rather than leaving it to
        // whatever the OS/theme defaults to), so Compose's own WindowInsets — status bar,
        // nav bar, and the keyboard — are the single source of truth for padding. Without
        // this, the system can additionally resize/pan the window on top of Compose's own
        // ime padding, which is what produced the gap between the keyboard and the composer.
        enableEdgeToEdge()
        preloadAntibioticGuidelines(applicationContext)
        setContent {
            MedtrixaTheme {
                MedtrixaRoot()
            }
        }
    }
}

@Composable
private fun MedtrixaRoot() {

    var moreTopicId by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    var currentScreen by rememberSaveable {
        mutableStateOf("home")
    }

    var selectedCalculatorId by rememberSaveable {
        mutableStateOf("")
    }

    var selectedDrugToolTitle by rememberSaveable {
        mutableStateOf("")
    }

    var selectedDrug by remember { mutableStateOf<String?>(null) }

    var antibioticGuidelinesReturnTo by rememberSaveable {
        mutableStateOf("antibiotic_dosing")
    }

    var bacterialAtlasReturnTo by rememberSaveable {
        mutableStateOf("antibiotic_dosing")
    }

    // Set when a score screen is opened from an AI answer, so Back returns to the
    // AI chat instead of the score list.
    var scoreReturnTo by rememberSaveable {
        mutableStateOf("")
    }

    var aiResume by rememberSaveable {
        mutableStateOf(false)
    }

    var drugDetailReturnTo by rememberSaveable {
        mutableStateOf("")
    }

    fun backFromScore(default: String) {
        if (scoreReturnTo.isNotEmpty()) {
            currentScreen = scoreReturnTo
            scoreReturnTo = ""
        } else {
            currentScreen = default
        }
    }

    // Phone/system Back button: does the same thing as each screen's own on-screen back arrow
    // (otherwise Android closes the app). On "home" it is disabled so Back exits the app.
    BackHandler(enabled = currentScreen != "home") {
        when (currentScreen) {
            "drug_detail" -> {
                if (drugDetailReturnTo.isNotEmpty()) {
                    currentScreen = drugDetailReturnTo
                    drugDetailReturnTo = ""
                } else {
                    currentScreen = "drug_tool_detail"
                }
            }
            "drug_tool_detail" -> currentScreen = "drug_tools"
            "pubmed", "antibiotic_stewardship", "antibiotic_dosing" -> currentScreen = "reference"
            "idsa_amr_guidance" -> currentScreen = "antibiotic_dosing"
            "antibiotic_dosing_guidelines" -> currentScreen = antibioticGuidelinesReturnTo
            "bacterial_atlas" -> currentScreen = bacterialAtlasReturnTo
            "clinical_scores", "risk_assessment" -> currentScreen = "assess"
            "curb65", "heart_score", "child_pugh", "centor", "meld30", "apgar", "gcs", "qsofa", "nihss" ->
                backFromScore("clinical_scores")
            "cha2ds2vasc", "hasbled", "wells_dvt", "wells_pe", "perc" ->
                backFromScore("risk_assessment")
            "cardiac_arrest", "abg_interpreter", "sepsis", "lab_interpreter",
            "pulse_assessment", "sbar_handover", "anaphylaxis", "stroke", "dka", "status_epilepticus" -> currentScreen = "emergency_protocols"
            "emergency_protocols", "primary_protocols", "diabetes", "pregnancy", "hypertension", "pancreatitis", "thyroid", "heart_failure" -> currentScreen = "tools"
            "insulin_dosing", "insulin_dosing_2" -> currentScreen = "diabetes"
        "ckd", "chronic_liver_disease", "mental_health", "autoimmune_disease", "coronary_artery_disease", "neurological_disease", "copd_asthma", "gout" -> currentScreen = "more"
            "more_topic" -> currentScreen = "healthcare_insights"
            "drug_tools", "reference", "assess", "calculators", "healthcare_insights" -> currentScreen = "toolkit"
            "calculator" -> currentScreen = "calculators"
            "ai_insights" -> {
                aiResume = false
                currentScreen = "toolkit"
            }
            else -> currentScreen = "home"
        }
    }

    val calculatorRepository = remember {
        CalculatorRepository()
    }

    val showBottomNavigation = currentScreen in setOf(
        "home", "toolkit", "tools", "primary_protocols", "diabetes", "pregnancy", "hypertension", "pancreatitis", "thyroid", "heart_failure", "insulin_dosing", "insulin_dosing_2", "emergency_protocols", "more", "ckd", "chronic_liver_disease", "mental_health", "autoimmune_disease", "coronary_artery_disease", "neurological_disease", "copd_asthma", "gout", "healthcare_insights"
    )
    val selectedDestination = when (currentScreen) {
        "home" -> "home"
        "toolkit" -> "toolkit"
        "healthcare_insights" -> "toolkit"
        "more", "ckd", "chronic_liver_disease", "mental_health", "autoimmune_disease", "coronary_artery_disease", "neurological_disease", "copd_asthma", "gout" -> "more"
        else -> "tools"
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomNavigation) {
                AppBottomNavigation(
                    selected = selectedDestination,
                    onHomeClick = { currentScreen = "home" },
                    onToolkitClick = { currentScreen = "toolkit" },
                    onToolsClick = { currentScreen = "tools" },
                    onMoreClick = { currentScreen = "more" }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            when (currentScreen) {

        "tools" -> {
            ProtocolsHubScreen(
                onBackClick = { currentScreen = "home" },
                onPrimaryClick = { currentScreen = "primary_protocols" },
                onEmergencyClick = { currentScreen = "emergency_protocols" },
                onInsulinClick = { currentScreen = "diabetes" },
                onPregnancyClick = { currentScreen = "pregnancy" },
                onHypertensionClick = { currentScreen = "hypertension" },
                onPancreatitisClick = { currentScreen = "pancreatitis" },
                onThyroidClick = { currentScreen = "thyroid" },
                onHeartFailureClick = { currentScreen = "heart_failure" }
            )
        }

        "primary_protocols" -> {
            ComingSoonScreen(title = "Primary Protocols", onBackClick = { currentScreen = "tools" })
        }

        "thyroid" -> {
            ComingSoonScreen(title = "Thyroid Disorders", onBackClick = { currentScreen = "tools" })
        }

        "pancreatitis" -> {
            ComingSoonScreen(title = "Pancreatitis", onBackClick = { currentScreen = "tools" })
        }

        "hypertension" -> {
            ComingSoonScreen(title = "Hypertension", onBackClick = { currentScreen = "tools" })
        }

        "heart_failure" -> {
            ComingSoonScreen(title = "Heart Failure", onBackClick = { currentScreen = "tools" })
        }

        "pregnancy" -> {
            ComingSoonScreen(title = "Pregnancy", onBackClick = { currentScreen = "tools" })
        }

        "diabetes" -> {
            DiabetesScreen(
                onBackClick = { currentScreen = "tools" },
                onInsulinClick = { currentScreen = "insulin_dosing" },
                onInsulin2Click = { currentScreen = "insulin_dosing_2" }
            )
        }

        "insulin_dosing" -> {
            InsulinDosingScreen(onBackClick = { currentScreen = "diabetes" })
        }
        "insulin_dosing_2" -> {
            InsulinDosing2Screen(onBackClick = { currentScreen = "diabetes" })
        }

        "emergency_protocols" -> {
            ProtocolsScreen(
                onBackClick = { currentScreen = "tools" },
                onCardiacArrestClick = { currentScreen = "cardiac_arrest" },
                onPulseClick = { currentScreen = "pulse_assessment" },
                onAbgClick = { currentScreen = "abg_interpreter" },
                onSepsisClick = { currentScreen = "sepsis" },
                onLabClick = { currentScreen = "lab_interpreter" },
                onSbarClick = { currentScreen = "sbar_handover" },
                onAnaphylaxisClick = { currentScreen = "anaphylaxis" },
                onStrokeClick = { currentScreen = "stroke" },
                onDkaClick = { currentScreen = "dka" },
                onStatusEpilepticusClick = { currentScreen = "status_epilepticus" }
            )
        }

        "lab_interpreter" -> {
            LabInterpreterScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "stroke" -> {
            StrokeScreen(onBackClick = { currentScreen = "emergency_protocols" }, onNihssClick = { scoreReturnTo = "stroke"; currentScreen = "nihss" })
        }

        "anaphylaxis" -> {
            AnaphylaxisScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "sbar_handover" -> {
            SbarHandoverScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "status_epilepticus" -> {
            StatusEpilepticusScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "dka" -> {
            DkaScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "sepsis" -> {
            SepsisScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "abg_interpreter" -> {
            AbgInterpreterScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "cardiac_arrest" -> {
            CardiacArrestScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "pulse_assessment" -> {
            PulseScreen(onBackClick = { currentScreen = "emergency_protocols" })
        }

        "drug_detail" -> {
            selectedDrug?.let { genericName ->
                DrugDetailScreen(
                    genericName = genericName,
                    onBackClick = {
                        if (drugDetailReturnTo.isNotEmpty()) {
                            currentScreen = drugDetailReturnTo
                            drugDetailReturnTo = ""
                        } else {
                            currentScreen = "drug_tool_detail"
                        }
                    }
                )
            }
        }

        "drug_tools" -> {
            DrugToolsScreen(
                onBackClick = { currentScreen = "toolkit" },
                onToolClick = { tool ->
                    selectedDrugToolTitle = tool.title
                    currentScreen = "drug_tool_detail"
                }
            )
        }

        "drug_tool_detail" -> {
            when (selectedDrugToolTitle) {
                "Dosage Calculator" -> WeightBasedDoseScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                "Renal Dose Adjustment" -> RenalDoseAdjustmentScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                "Pregnancy & Lactation" -> PregnancyLactationScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                "Half-life & Washout" -> HalfLifeWashoutScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                "Pediatric Dosing" -> PediatricDoseScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                "IV Infusion Rate Calculator" -> InfusionRateScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                "Drug Interaction Checker" -> InteractionCheckerScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                "Therapeutic Alternatives" -> TherapeuticAlternativesScreen(
                    onBackClick = { currentScreen = "drug_tools" },
                    onDrugClick = { hit ->
                        selectedDrug = hit.name
                        drugDetailReturnTo = "drug_tool_detail"
                        currentScreen = "drug_detail"
                    }
                )
                "Contraindication Checker" -> ContraindicationCheckerScreen(
                    onBackClick = { currentScreen = "drug_tools" }
                )
                    "Drug Search" -> DrugSearchScreen(
                        onBackClick = { currentScreen = "drug_tools" },
                        onDrugClick = { drug ->
                            selectedDrug = drug
                            currentScreen = "drug_detail"
                        }
                    )
                else -> ComingSoonScreen(
                    title = selectedDrugToolTitle,
                    onBackClick = { currentScreen = "drug_tools" }
                )
            }
        }

        "reference" -> {
            ReferenceScreen(
                onBackClick = { currentScreen = "toolkit" },
                onReferenceClick = { referenceId ->
                    if (referenceId == "antibiotic_dosing") {
                        currentScreen = "antibiotic_dosing"
                    } else if (referenceId == "antibiotic_stewardship") {
                        currentScreen = "antibiotic_stewardship"
                    } else if (referenceId == "pubmed") {
                        currentScreen = "pubmed"
                    }
                }
            )
        }

        "pubmed" -> {
            PubMedScreen(
                onBackClick = { currentScreen = "reference" }
            )
        }

        "antibiotic_stewardship" -> {
            AntibioticStewardshipReferenceScreen(
                onBackClick = { currentScreen = "reference" }
            )
        }

        "antibiotic_dosing" -> {
            AntimicrobialReferenceHubScreen(
                onBackClick = { currentScreen = "reference" },
                onBacterialAtlasClick = { currentScreen = "bacterial_atlas" },
                onIdsaAmrClick = { currentScreen = "idsa_amr_guidance" },
                onGuidelinesClick = {
                    antibioticGuidelinesReturnTo = "antibiotic_dosing"
                    currentScreen = "antibiotic_dosing_guidelines"
                }
            )
        }

        "antibiotic_dosing_guidelines" -> {
            AntibioticDosingGuidelinesScreen(
                onBackClick = { currentScreen = antibioticGuidelinesReturnTo }
            )
        }

        "idsa_amr_guidance" -> {
            AntibioticDosingGuidelinesScreen(
                onBackClick = { currentScreen = "antibiotic_dosing" },
                idsaOnly = true
            )
        }

        "bacterial_atlas" -> {
            BacterialAtlasScreen(
                onBackClick = { currentScreen = bacterialAtlasReturnTo }
            )
        }

        "assess" -> {
            AssessScreen(
                onBackClick = { currentScreen = "toolkit" },
                onClinicalScoresClick = { currentScreen = "clinical_scores" },
                onRiskAssessmentClick = { currentScreen = "risk_assessment" }
            )
        }

        "clinical_scores" -> {
            ClinicalScoresScreen(
                onBackClick = { currentScreen = "assess" },
                onScoreClick = { scoreId ->
                    currentScreen = scoreId
                }
            )
        }

        "risk_assessment" -> {
            RiskAssessmentScreen(
                onBackClick = { currentScreen = "assess" },
                onToolClick = { toolId ->
                    currentScreen = toolId
                }
            )
        }

        "curb65" -> { Curb65Screen(onBackClick = { backFromScore("clinical_scores") }) }
        "heart_score" -> { HeartScoreScreen(onBackClick = { backFromScore("clinical_scores") }) }
        "child_pugh" -> { ChildPughScreen(onBackClick = { backFromScore("clinical_scores") }) }
        "centor" -> { CentorScoreScreen(onBackClick = { backFromScore("clinical_scores") }) }
        "meld30" -> { Meld30Screen(onBackClick = { backFromScore("clinical_scores") }) }
        "apgar" -> { ApgarScreen(onBackClick = { backFromScore("clinical_scores") }) }
        "nihss" -> { NihssScreen(onBackClick = { backFromScore("clinical_scores") }) }
        "gcs" -> { GcsScreen(onBackClick = { backFromScore("clinical_scores") }) }
        "qsofa" -> { QsofaScreen(onBackClick = { backFromScore("clinical_scores") }) }

        "cha2ds2vasc" -> { Cha2ds2VascScreen(onBackClick = { backFromScore("risk_assessment") }) }
        "hasbled" -> { HasBledScreen(onBackClick = { backFromScore("risk_assessment") }) }
        "wells_dvt" -> { WellsDvtScreen(onBackClick = { backFromScore("risk_assessment") }) }
        "wells_pe" -> { WellsPeScreen(onBackClick = { backFromScore("risk_assessment") }) }
        "perc" -> { PercScreen(onBackClick = { backFromScore("risk_assessment") }) }

        "calculators" -> {
            CalculatorLibraryScreen(
                repository = calculatorRepository,
                onBackClick = { currentScreen = "toolkit" },
                onCalculatorClick = { calculator ->
                    selectedCalculatorId = calculator.id
                    currentScreen = "calculator"
                }
            )
        }

        "calculator" -> {
            CalculatorScreen(
                calculatorId = selectedCalculatorId,
                onBackClick = { currentScreen = "calculators" }
            )
        }

        "ai_insights" -> {
            AiInsightsScreen(
                onBackClick = {
                    aiResume = false
                    currentScreen = "toolkit"
                },
                resumeLast = aiResume,
                onOpenScore = { id ->
                    scoreReturnTo = "ai_insights"
                    aiResume = true
                    currentScreen = id
                }
            )
        }

        "healthcare_insights" -> {
            MoreTopicsScreen(
                onHomeClick = { currentScreen = "toolkit" },
                onToolsClick = { currentScreen = "tools" },
                onTopicClick = { id ->
                    moreTopicId = id
                    currentScreen = "more_topic"
                }
            )
        }

        "more" -> {
            ConditionsScreen(
                onBackClick = { currentScreen = "home" },
                onCkdClick = { currentScreen = "ckd" },
                onChronicLiverDiseaseClick = { currentScreen = "chronic_liver_disease" },
                onMentalHealthClick = { currentScreen = "mental_health" },
                onCopdAsthmaClick = { currentScreen = "copd_asthma" },
                onCadClick = { currentScreen = "coronary_artery_disease" },
                onGoutClick = { currentScreen = "gout" },
                onNeuroClick = { currentScreen = "neurological_disease" },
                onAutoimmuneClick = { currentScreen = "autoimmune_disease" }
            )
        }

        "chronic_liver_disease" -> {
            ComingSoonScreen(title = "Chronic Liver Disease", onBackClick = { currentScreen = "more" })
        }

            "mental_health" -> {
                ComingSoonScreen(title = "Mental Health Conditions", onBackClick = { currentScreen = "more" })
            }

            "copd_asthma" -> {
                ComingSoonScreen(title = "COPD & Asthma", onBackClick = { currentScreen = "more" })
            }

            "coronary_artery_disease" -> {
                ComingSoonScreen(title = "Coronary Artery Disease", onBackClick = { currentScreen = "more" })
            }

            "gout" -> {
                ComingSoonScreen(title = "Gout", onBackClick = { currentScreen = "more" })
            }

            "neurological_disease" -> {
                ComingSoonScreen(title = "Neurological Disease", onBackClick = { currentScreen = "more" })
            }

            "autoimmune_disease" -> {
                ComingSoonScreen(title = "Autoimmune Disease", onBackClick = { currentScreen = "more" })
            }

        "ckd" -> {
            ComingSoonScreen(title = "Chronic Kidney Disease", onBackClick = { currentScreen = "more" })
        }

        "more_topic" -> {
            MoreTopicDetailScreen(
                topicId = moreTopicId,
                onBackClick = { currentScreen = "healthcare_insights" }
            )
        }

        "clinical_sources" -> {
            ClinicalSourcesScreen(onBackClick = { currentScreen = "home" })
        }

        else -> {
            HomeScreen(
                page = if (currentScreen == "toolkit") HomePage.Toolkit else HomePage.Drugs,
                onToolsClick = { currentScreen = "tools" },
                onMoreClick = { currentScreen = "more" },
                onClinicalSourcesClick = { currentScreen = "clinical_sources" },
                onCalculateClick = { currentScreen = "calculators" },
                onAssessClick = { currentScreen = "assess" },
                onDrugToolsClick = { currentScreen = "drug_tools" },
                onDrugClick = { drug ->
                    selectedDrug = drug
                    drugDetailReturnTo = "home"
                    currentScreen = "drug_detail"
                },
                onReferenceClick = { currentScreen = "reference" },
                onHealthcareInsightsClick = { currentScreen = "healthcare_insights" },
                onAiInsightsClick = {
                    aiResume = false
                    currentScreen = "ai_insights"
                }
            )
        }
            }
        }
    }
}
