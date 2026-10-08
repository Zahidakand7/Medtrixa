package com.medtrixa.app

import com.medtrixa.app.ui.screens.preloadAntibioticGuidelines
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.medtrixa.app.ui.screens.drugtools.TherapeuticAlternativesScreen
import com.medtrixa.app.ui.screens.drugtools.InteractionCheckerScreen
import com.medtrixa.app.ui.screens.drugtools.DuplicateTherapyScreen
import com.medtrixa.app.ui.screens.drugtools.ContraindicationCheckerScreen
import com.medtrixa.app.ui.screens.calculators.WeightBasedDoseScreen
import com.medtrixa.app.ui.screens.calculators.CreatinineClearanceScreen
import com.medtrixa.app.ui.screens.calculators.PediatricDoseScreen
import com.medtrixa.app.ui.screens.calculators.InfusionRateScreen
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
import com.medtrixa.app.ui.screens.HomeScreen
import com.medtrixa.app.ui.screens.AiInsightsScreen
import com.medtrixa.app.ui.screens.ToolsScreen
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
            "antibiotic_dosing_guidelines" -> currentScreen = antibioticGuidelinesReturnTo
            "bacterial_atlas" -> currentScreen = bacterialAtlasReturnTo
            "clinical_scores", "risk_assessment" -> currentScreen = "assess"
            "curb65", "heart_score", "child_pugh", "centor", "meld30", "apgar", "gcs", "qsofa" ->
                backFromScore("clinical_scores")
            "cha2ds2vasc", "hasbled", "wells_dvt", "wells_pe" ->
                backFromScore("risk_assessment")
            "calculator" -> currentScreen = "calculators"
            "ai_insights" -> {
                aiResume = false
                currentScreen = "home"
            }
            else -> currentScreen = "home"
        }
    }

    val calculatorRepository = remember {
        CalculatorRepository()
    }

    when (currentScreen) {

        "tools" -> {
            ComingSoonScreen(
                title = "Tools",
                onBackClick = { currentScreen = "home" }
            )
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
                onBackClick = { currentScreen = "home" },
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
                "Duplicate Therapy Checker" -> DuplicateTherapyScreen(
                    onBackClick = { currentScreen = "drug_tools" }
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
                onBackClick = { currentScreen = "home" },
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

        "bacterial_atlas" -> {
            BacterialAtlasScreen(
                onBackClick = { currentScreen = bacterialAtlasReturnTo }
            )
        }

        "assess" -> {
            AssessScreen(
                onBackClick = { currentScreen = "home" },
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
        "gcs" -> { GcsScreen(onBackClick = { backFromScore("clinical_scores") }) }
        "qsofa" -> { QsofaScreen(onBackClick = { backFromScore("clinical_scores") }) }

        "cha2ds2vasc" -> { Cha2ds2VascScreen(onBackClick = { backFromScore("risk_assessment") }) }
        "hasbled" -> { HasBledScreen(onBackClick = { backFromScore("risk_assessment") }) }
        "wells_dvt" -> { WellsDvtScreen(onBackClick = { backFromScore("risk_assessment") }) }
        "wells_pe" -> { WellsPeScreen(onBackClick = { backFromScore("risk_assessment") }) }

        "calculators" -> {
            CalculatorLibraryScreen(
                repository = calculatorRepository,
                onBackClick = { currentScreen = "home" },
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
                    currentScreen = "home"
                },
                resumeLast = aiResume,
                onOpenScore = { id ->
                    scoreReturnTo = "ai_insights"
                    aiResume = true
                    currentScreen = id
                }
            )
        }

        "more" -> {
            HomeScreen(
                onToolsClick = { currentScreen = "tools" },
                onMoreClick = { currentScreen = "more" },
                onCalculateClick = { currentScreen = "calculators" },
                onAssessClick = { currentScreen = "assess" },
                onDrugToolsClick = { currentScreen = "drug_tools" },
                onDrugClick = { drug ->
                    selectedDrug = drug
                    drugDetailReturnTo = "more"
                    currentScreen = "drug_detail"
                },
                onReferenceClick = { currentScreen = "reference" },
                onAiInsightsClick = {
                    aiResume = false
                    currentScreen = "ai_insights"
                }
            )
        }

        else -> {
            HomeScreen(
                onToolsClick = { currentScreen = "tools" },
                onMoreClick = { currentScreen = "more" },
                onCalculateClick = { currentScreen = "calculators" },
                onAssessClick = { currentScreen = "assess" },
                onDrugToolsClick = { currentScreen = "drug_tools" },
                onDrugClick = { drug ->
                    selectedDrug = drug
                    drugDetailReturnTo = "home"
                    currentScreen = "drug_detail"
                },
                onReferenceClick = { currentScreen = "reference" },
                onAiInsightsClick = {
                    aiResume = false
                    currentScreen = "ai_insights"
                }
            )
        }
    }
}
