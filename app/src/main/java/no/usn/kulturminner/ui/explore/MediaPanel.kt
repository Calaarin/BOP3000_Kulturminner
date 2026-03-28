package no.usn.kulturminner.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MediaPanel(
    uiState: ExploreUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Medieinnhold her
        Text("Mediapanel – scroll uavhengig av kartet")
        Spacer(Modifier.height(8.dp))
        // Foreløpig bare liste som eksempeltekst
        repeat(20) { i ->
            Text("Element $i", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}