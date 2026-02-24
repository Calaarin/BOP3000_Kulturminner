package no.usn.kulturminner.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OverviewScreen(
    uiState: OverviewUiState,
    onCreatePointClick: () -> Unit,
    onEditPointClick: () -> Unit,
    onEditRouteClick: () -> Unit
) {

    // Bruker conditional rendering for å vise eventuell loading, error, eller mangel på innhold
    // when-setning er Kotlins versjon av switch-setning fra Java (med ekstra features). Alternativ: if-else.

    when {
        // Hvis data laster
        uiState.isLoading -> {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        // Hvis det er error ved henting av data
        uiState.error != null -> {
            Text(
                text = "Feil: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Hvis man har mottat data lages innholdet
        uiState.demoPoints.isNotEmpty() -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Oversiktskort
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column {
                                    Text(
                                        text = "Oversikt",
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Opplevelsespunkt: 3")
                                    Text("Ruter: 2")
                                }

                                Button(
                                    onClick = onCreatePointClick
                                ) {
                                    Text("Nytt punkt")
                                }
                            }
                        }
                    }
                }

                // Action cards (alternerende farger)
                items(uiState.demoPoints.size) { index ->

                    val backgroundColor =
                        if (index % 2 == 0)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            Color(0xFFE3F2FD) // lys blå

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = backgroundColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = uiState.demoPoints[index],
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = onEditPointClick
                                ) {
                                    Text("Rediger punkt")
                                }

                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = onEditRouteClick
                                ) {
                                    Text("Rediger rute")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Hvis det ikke finnes data
        else -> {
            Text(
                "Ingen data tilgjengelig",
                modifier = Modifier.padding(16.dp)
            )
        }

    }


}