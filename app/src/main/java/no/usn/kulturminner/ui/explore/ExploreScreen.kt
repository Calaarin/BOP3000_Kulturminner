package no.usn.kulturminner.ui.explore

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import no.usn.kulturminner.ui.components.BaseMap

@Composable
fun ExploreScreen(
    uiState: ExploreUiState
) {
    // Posisjonstillatelser fra bruker
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        // når granted er true starter posisjon automatisk via ViewModel
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Kart i bakgrunnen – fyller hele skjermen
        BaseMap(
            modifier = Modifier.fillMaxSize(),
            initialLat = 59.41,
            initialLng = 9.06,
            initialZoom = 12.0,
            onMapReady = { map ->
                // Legg til punktsymboler, brukerposisjon osv.
                // Dette er ExploreScreen-spesifikt
            }
        )

        // Mediapanel som overlay nederst – kun synlig når bruker er nær punkt
        // (foreløpig alltid synlig for testing)
        MediaPanel(
            uiState = uiState,
            point = uiState.activePoint,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f) // ca 45% av skjermen
                .align(Alignment.BottomCenter)
        )
    }
}