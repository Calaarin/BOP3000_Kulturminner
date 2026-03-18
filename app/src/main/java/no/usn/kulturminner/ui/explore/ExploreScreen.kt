package no.usn.kulturminner.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ExploreScreen(
    uiState: ExploreUiState
) {

    Column(modifier = Modifier.fillMaxSize()) {

        // Kartdel – tar halve skjermen, scrolles ikke
        ExploreMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),  // 50% av skjermen
            initialLat = 59.41,
            initialLng = 9.06,
            initialZoom = 12.0
        )

        // Mediadel – tar andre halvdelen, kan scrolles uavhengig
        MediaPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}