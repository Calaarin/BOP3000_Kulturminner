package no.usn.kulturminner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import no.usn.kulturminner.ui.navigation.AppNavHost
import no.usn.kulturminner.ui.theme.KulturminnerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KulturminnerTheme {
                AppNavHost()
            }
        }
    }
}