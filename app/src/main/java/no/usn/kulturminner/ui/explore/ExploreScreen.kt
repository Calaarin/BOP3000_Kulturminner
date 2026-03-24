package no.usn.kulturminner.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun ExploreScreen(
    uiState: ExploreUiState
) {

    Box(modifier = Modifier.fillMaxSize()) {

        // Kart i bakgrunnen – fyller hele skjermen
        ExploreMap(
            modifier = Modifier.fillMaxSize(),
            initialLat = 59.41,
            initialLng = 9.06,
            initialZoom = 12.0
        )

        // Mediapanel som overlay nederst – skal kun være synlig når bruker er nær punkt
        // (foreløpig alltid synlig for testing)
        MediaPanel(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.BottomCenter)
                .nestedScroll(remember { NoOpScrollConnection() })  // blokkerer oppover-scroll
        )
    }
}

private class NoOpScrollConnection : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
}