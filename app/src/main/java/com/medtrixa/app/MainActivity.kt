package com.medtrixa.app

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
import com.medtrixa.app.ui.screens.assess.Curb65Screen
import com.medtrixa.app.ui.screens.assess.Cha2ds2VascScreen
import com.medtrixa.app.ui.screens.HomeScreen
import com.medtrixa.app.ui.screens.ToolsScreen
import com.medtrixa.app.ui.theme.MedtrixaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    val calculatorRepository = remember {
        CalculatorRepository()
    }

    when (currentScreen) {

        "tools" -> {
            ToolsScreen(
                onHomeClick = {
                    currentScreen = "home"
                },
                onMoreClick = {
                    currentScreen = "more"
                },
                onCalculateClick = {
                    currentScreen = "calculators"
                },
                onAssessClick = {
                    currentScreen = "assess"
                }
            )
        }

        "assess" -> {
            AssessScreen(
                onBackClick = {
                    currentScreen = "tools"
                },
                onToolClick = { toolId ->
                    when (toolId) {
                                    "curb65" -> currentScreen = "curb65"
                                    "cha2ds2vasc" -> currentScreen = "cha2ds2vasc"
                                }
                }
            )
        }

        "curb65" -> {
            Curb65Screen(
                onBackClick = {
                    currentScreen = "assess"
                }
            )
        }

        "cha2ds2vasc" -> {
            Cha2ds2VascScreen(
                onBackClick = {
                    currentScreen = "assess"
                }
            )
        }
        "calculators" -> {
            CalculatorLibraryScreen(
                repository = calculatorRepository,
                onBackClick = {
                    currentScreen = "tools"
                },
                onCalculatorClick = { calculator: MedicalCalculator ->
                    selectedCalculatorId = calculator.id
                    currentScreen = "calculator"
                }
            )
        }

        "calculator" -> {
            CalculatorScreen(
                calculatorId = selectedCalculatorId,
                onBackClick = {
                    currentScreen = "calculators"
                }
            )
        }

        "more" -> {
            HomeScreen(
                onToolsClick = {
                    currentScreen = "tools"
                },
                onMoreClick = {
                    currentScreen = "more"
                }
            )
        }

        else -> {
            HomeScreen(
                onToolsClick = {
                    currentScreen = "tools"
                },
                onMoreClick = {
                    currentScreen = "more"
                }
            )
        }
    }
}
