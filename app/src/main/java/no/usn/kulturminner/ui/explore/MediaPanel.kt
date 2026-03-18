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
import androidx.compose.ui.unit.dp

@Composable
fun MediaPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Ditt medieinnhold her
        Text("Mediapanel – scroll uavhengig av kartet")
        Spacer(Modifier.height(8.dp))
        // f.eks. bilder, beskrivelser, turtips osv.
        repeat(20) { i ->
            Text("Element $i", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}