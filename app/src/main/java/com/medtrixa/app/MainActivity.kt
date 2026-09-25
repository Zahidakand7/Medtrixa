package com.medtrixa.app

import com.medtrixa.app.ui.screens.preloadAntibioticGuidelines
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.medtrixa.app.data.db.DrugEntity
import com.medtrixa.app.ui.screens.drugtools.InteractionCheckerScreen
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
                    onBackClick = { currentScreen = "drug_tool_detail" }
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
                "Renal Dose Adjustment" -> CreatinineClearanceScreen(
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

        "curb65" -> { Curb65Screen(onBackClick = { currentScreen = "clinical_scores" }) }
        "heart_score" -> { HeartScoreScreen(onBackClick = { currentScreen = "clinical_scores" }) }
        "child_pugh" -> { ChildPughScreen(onBackClick = { currentScreen = "clinical_scores" }) }
        "centor" -> { CentorScoreScreen(onBackClick = { currentScreen = "clinical_scores" }) }
        "meld30" -> { Meld30Screen(onBackClick = { currentScreen = "clinical_scores" }) }
        "apgar" -> { ApgarScreen(onBackClick = { currentScreen = "clinical_scores" }) }
        "gcs" -> { GcsScreen(onBackClick = { currentScreen = "clinical_scores" }) }
        "qsofa" -> { QsofaScreen(onBackClick = { currentScreen = "clinical_scores" }) }

        "cha2ds2vasc" -> { Cha2ds2VascScreen(onBackClick = { currentScreen = "risk_assessment" }) }
        "hasbled" -> { HasBledScreen(onBackClick = { currentScreen = "risk_assessment" }) }
        "wells_dvt" -> { WellsDvtScreen(onBackClick = { currentScreen = "risk_assessment" }) }
        "wells_pe" -> { WellsPeScreen(onBackClick = { currentScreen = "risk_assessment" }) }

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
                onBackClick = { currentScreen = "home" }
            )
        }

        "more" -> {
            HomeScreen(
                onToolsClick = { currentScreen = "tools" },
                onMoreClick = { currentScreen = "more" },
                onCalculateClick = { currentScreen = "calculators" },
                onAssessClick = { currentScreen = "assess" },
                onDrugToolsClick = { currentScreen = "drug_tools" },
                onReferenceClick = { currentScreen = "reference" },
                onAiInsightsClick = { currentScreen = "ai_insights" }
            )
        }

        else -> {
            HomeScreen(
                onToolsClick = { currentScreen = "tools" },
                onMoreClick = { currentScreen = "more" },
                onCalculateClick = { currentScreen = "calculators" },
                onAssessClick = { currentScreen = "assess" },
                onDrugToolsClick = { currentScreen = "drug_tools" },
                onReferenceClick = { currentScreen = "reference" },
                onAiInsightsClick = { currentScreen = "ai_insights" }
            )
        }
    }
}
