package no.usn.kulturminner.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.time.ZoneId

@Composable
fun OverviewScreen(
    uiState: OverviewUiState,
    onCreatePointClick: () -> Unit,
    onEditPointClick: (String) -> Unit,
    onDeletePointClick: (String) -> Unit,
    onSortAlphabetically: () -> Unit,
    onSortByDate: () -> Unit
) {
    when {
        uiState.isUserLoading || uiState.isPointListLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F1F8))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // === ADMIN HEADER ===
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE3DDF6)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF6F63D9)
                                    ) {
                                        Text(
                                            text = "ADMIN",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { /* TODO: Endre passord */ },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Endre passord")
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${uiState.user?.firstName} ${uiState.user?.lastName}",
                                    style = MaterialTheme.typography.headlineSmall
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "@${uiState.user?.username}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "Administrasjon",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // === OPPLEVELSESPUNKTER HEADER ===
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE7E7E7)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Opplevelsespunkter",
                                        style = MaterialTheme.typography.headlineSmall
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF9ED8F7), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = uiState.points.size.toString(),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = onSortAlphabetically,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1E9B))
                                    ) {
                                        Text("Sorter alfabetisk")
                                    }

                                    Button(
                                        onClick = onSortByDate,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1E9B))
                                    ) {
                                        Text("Sorter etter dato")
                                    }
                                }
                            }
                        }
                    }

                    // === LISTE MED PUNKTER ===
                    items(uiState.points.size) { index ->
                        val point = uiState.points[index]

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = point.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    val date = point.updatedAt?.let {
                                        DateTimeFormatter.ofPattern("dd. MMM yyyy")
                                            .withZone(java.time.ZoneId.systemDefault())
                                            .format(it)
                                    } ?: ""
                                    Text(
                                        text = "Endret $date",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { onEditPointClick(point.id ?: "") },   // sender id-en
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.DarkGray)
                                    }

                                    IconButton(
                                        onClick = { onDeletePointClick(point.id ?: "") },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5A5A))
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Action Button
                FloatingActionButton(
                    onClick = onCreatePointClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp)
                        .navigationBarsPadding()
                        .offset(y = (-16).dp)
                        .size(72.dp),
                    shape = RoundedCornerShape(18.dp),
                    containerColor = Color(0xFF9B8CFF),
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(36.dp))
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