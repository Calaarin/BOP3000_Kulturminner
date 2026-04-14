package no.usn.kulturminner.ui.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.usn.kulturminner.R
import no.usn.kulturminner.data.model.Section

@Composable
fun MediaPanel(
    uiState: ExploreUiState,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isPointListLoading -> {
            Box(
                modifier = modifier.fillMaxWidth().height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.pointError != null -> {
            Box(
                modifier = modifier.fillMaxWidth().height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Feil: ${uiState.pointError}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        uiState.pointNearby != null -> {
            val point = uiState.pointNearby

            Column(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Tittel på punktet
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = point.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(34.dp))

                // Iterer gjennom alle seksjoner
                point.sections.forEachIndexed { index, section ->
                    SectionContent(section, index)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        else -> {
            Box(
                modifier = modifier.fillMaxWidth().height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ingen opplevelsespunkt i nærheten",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun SectionContent(section: Section, index: Int) {
    Column {
        // Heading
        section.heading?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tekst
        section.text?.takeIf { it.isNotBlank() }?.let {
            Text(text = it)
            Spacer(modifier = Modifier.height(42.dp))
        }

        // Bilde – bruker lokalt drawable basert på seksjonsindeks
        if (!section.imageUrl.isNullOrBlank()) {
            val drawableId = when (index % 5) {
                0 -> R.drawable.gullbring_1
                1 -> R.drawable.gullbring_2
                2 -> R.drawable.gullbring_3
                3 -> R.drawable.gullbring_4
                else -> R.drawable.gullbring_5
            }

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = section.heading ?: "Bilde fra Gullbring",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // For video viser vi bare ingenting foreløpig
    }
}