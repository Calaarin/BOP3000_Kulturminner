package no.usn.kulturminner.ui.overview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OverviewScreen(
    uiState: OverviewUiState,
    onCreatePointClick: () -> Unit,
    onEditPointClick: () -> Unit,
    onEditRouteClick: () -> Unit,
    onSortAlphabetically: () -> Unit,
    onSortByDate: () -> Unit
) {
    when {
        // Loading state (viser generell loading hvis både bruker og punkter lastes)
        uiState.isUserLoading || uiState.isPointListLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Error state (prioriterer punkt-feil siden det er hovedinnholdet)
        uiState.pointError != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Feil: ${uiState.pointError}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        uiState.points.isNotEmpty() -> {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Oversikt",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    Column {
                                        Text(
                                            text = uiState.points.size.toString(),
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Text(
                                            text = "Punkter",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "0",           // Ruter teller – kan utvides senere
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Text(
                                            text = "Ruter",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onSortAlphabetically,
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Sorter alfabetisk")
                                    }

                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onSortByDate,
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Sorter etter dato")
                                    }
                                }
                            }
                        }
                    }

                    // Punkter i listen
                    items(uiState.points.size) { index ->
                        val point = uiState.points[index]
                        val backgroundColor = if (index % 2 == 0) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            Color(0xFFEAF4FF)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = point.title,
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = onEditPointClick,
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Rediger punkt")
                                    }

                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onEditRouteClick,
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Rediger rute")
                                    }
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = onCreatePointClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("+")
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ingen opplevelsespunkt enda",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}