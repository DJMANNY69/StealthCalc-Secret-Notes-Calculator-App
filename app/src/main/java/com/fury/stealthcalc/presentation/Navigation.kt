package com.fury.stealthcalc.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.biometric.BiometricPrompt
import com.fury.stealthcalc.presentation.add_edit_note.AddEditNoteScreen
import com.fury.stealthcalc.presentation.calculator.CalculatorScreen
import com.fury.stealthcalc.presentation.calculator.CalculatorUiEvent
import com.fury.stealthcalc.presentation.calculator.CalculatorViewModel
import com.fury.stealthcalc.presentation.vault.VaultScreen
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("ContextCastToActivity")
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "calculator_screen") {

        // 1. Calculator
        composable(
            "calculator_screen",
            exitTransition = {
                // When going to Vault, Slide Up out of view
                if (targetState.destination.route == "vault_screen") {
                    slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(500))
                } else null
            },
            popEnterTransition = {
                // When coming back from Vault, Slide Down into view
                if (initialState.destination.route == "vault_screen") {
                    slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500))
                } else null
            }
        ) {
            val viewModel: CalculatorViewModel = hiltViewModel()
            val context = LocalContext.current as FragmentActivity
            val executor = ContextCompat.getMainExecutor(context)

            // Setup Biometric
            val biometricPrompt = remember {
                BiometricPrompt(context, executor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        viewModel.onBiometricSuccess()
                    }
                })
            }
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Stealth Access")
                .setSubtitle("Authenticate to access the Vault")
                .setNegativeButtonText("Cancel")
                .build()

            LaunchedEffect(true) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is CalculatorUiEvent.ShowBiometricPrompt -> biometricPrompt.authenticate(promptInfo)
                        is CalculatorUiEvent.NavigateToVault -> navController.navigate("vault_screen")
                    }
                }
            }
            CalculatorScreen(viewModel = viewModel, onNavigateToVault = {})
        }

        // 2. Vault List
        composable(
            "vault_screen",
            enterTransition = {
                // Only slide UP if coming from Calculator
                if (initialState.destination.route == "calculator_screen") {
                    slideInVertically(initialOffsetY = { it }, animationSpec = tween(500))
                } else {
                    // If coming back from Note, standard fade/slide
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                }
            },
            exitTransition = {
                // When going to Note, slide to Left
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                // When coming back from Note, slide in from Left
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                // When going back to Calculator, slide down
                slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
            }
        ) {
            VaultScreen(navController = navController)
        }

        // 3. Add/Edit Note Screen
        composable(
            route = "add_edit_note_screen?noteId={noteId}&noteColor={noteColor}",
            arguments = listOf(
                navArgument(name = "noteId") { type = NavType.IntType; defaultValue = -1 },
                navArgument(name = "noteColor") { type = NavType.IntType; defaultValue = -1 }
            ),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
            val color = it.arguments?.getInt("noteColor") ?: -1
            AddEditNoteScreen(navController = navController, noteColor = color)
        }
    }
}