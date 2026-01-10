package com.fury.stealthcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fury.stealthcalc.presentation.calculator.CalculatorScreen
import com.fury.stealthcalc.ui.theme.StealthCalcTheme // Ensure your theme name matches project name
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // <--- VERY IMPORTANT FOR HILT
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Note: We are not using the custom theme yet to save time,
            // but you can wrap this in your theme block.
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                CalculatorScreen(
                    onNavigateToVault = {
                        // TODO: Implement navigation later
                    }
                )
            }
        }
    }
}