package no.usn.kulturminner.ui.editpoint

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditPointScreen(
    uiState: EditPointUiState
) {
    when {
        // Hvis data laster fra server
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Hvis det er error ved henting av data
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Feil: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Hvis man har mottat data lages innholdet
        uiState.pointId != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Rediger punkt",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Må følge samme skjemaformat som CreatePointScreen",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Tittel: ${uiState.title}",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Bytt ut med det egentlige skjemaet senere
                // (samme felter som i CreatePointScreen)
            }
        }

        // Ingen data (f.eks. ugyldig ID eller feil ved lasting)
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ingen punkt å redigere",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}