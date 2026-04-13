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
import no.usn.kulturminner.data.model.Point

@Composable
fun MediaPanel(
    uiState: ExploreUiState,
    point: Point?,
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

        if (point == null) {
            Text("Ingen punkt valgt")
            return@Column
        }

        // ======== HOVEDINFO ========
        Text(
            text = point.title,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(8.dp))

        Text("ID: ${point.id}")
        Text("Lat: ${point.lat}")
        Text("Lng: ${point.lng}")
        Text("Radius: ${point.radius} m")

        if (!point.audioUrl.isNullOrBlank()) {
            Text("Audio URL: ${point.audioUrl}")
        }

        if (point.createdAt != null) {
            Text("Created: ${point.createdAt}")
        }

        if (point.updatedAt != null) {
            Text("Updated: ${point.updatedAt}")
        }

        Spacer(Modifier.height(16.dp))

        // ======== SEKSJONER ========
        Text(
            text = "Seksjoner",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        point.sections.forEachIndexed { index, section ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {

                Text(
                    text = "Seksjon ${index + 1}",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(4.dp))

                if (!section.heading.isNullOrBlank()) {
                    Text("Heading: ${section.heading}")
                }

                if (!section.text.isNullOrBlank()) {
                    Text("Text: ${section.text}")
                }

                if (!section.imageUrl.isNullOrBlank()) {
                    Text("Image URL: ${section.imageUrl}")
                }

                if (!section.videoUrl.isNullOrBlank()) {
                    Text("Video URL: ${section.videoUrl}")
                }
            }
        }
    }
}