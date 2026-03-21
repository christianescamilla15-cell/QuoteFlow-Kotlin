package com.christianhernandez.quoteflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.christianhernandez.quoteflow.navigation.QuoteFlowNavHost
import com.christianhernandez.quoteflow.ui.theme.QuoteFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            var language by remember { mutableStateOf("en") }

            QuoteFlowTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuoteFlowNavHost(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { isDarkMode = it },
                        language = language,
                        onLanguageChange = { language = it },
                    )
                }
            }
        }
    }
}
