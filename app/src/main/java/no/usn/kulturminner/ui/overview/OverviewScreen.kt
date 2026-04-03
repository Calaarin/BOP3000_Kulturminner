package no.usn.kulturminner.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OverviewScreen(
    uiState: OverviewUiState,
    onCreatePointClick: () -> Unit,
    onEditPointClick: () -> Unit,
    onEditRouteClick: () -> Unit,
    onSortAlphabetically: () -> Unit,
    onSortByDate: () -> Unit,
    onMediaPanelClick: () -> Unit,
    onAddMediaTextClick: () -> Unit
) {
    // Definering av brand-fargen din
    val brandPurple = Color(0xFF7615EA)
    val backgroundLight = Color(0xFFF8F9FE)

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(backgroundLight),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = brandPurple)
            }
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize().background(backgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Feil: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        uiState.demoPoints.isNotEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize().background(backgroundLight)
            ) {
                // Her er nøkkelen til scrolling: Alt ligger inni LazyColumn
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {

                    // --- TOPP-BANNER (MODERNE LOOK) ---
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brandPurple)
                                .padding(horizontal = 20.dp, vertical = 32.dp)
                        ) {
                            Text(
                                text = "Administrasjon",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Håndter dine kulturminner",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // --- OVERSIKTSKORT (STATISTIKK) ---
                    item {
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Oversikt",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                    Column {
                                        Text(text = uiState.demoPoints.size.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = brandPurple)
                                        Text(text = "Punkter", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Column {
                                        Text(text = "0", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(text = "Ruter", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onSortAlphabetically,
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.SortByAlpha, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("A-Å", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onSortByDate,
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Dato", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    // --- LISTEN MED PUNKTER ---
                    items(uiState.demoPoints.size) { index ->
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = uiState.demoPoints[index],
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // DIN VIKTIGSTE KNAPP: MEDIAPANEL
                                    Button(
                                        modifier = Modifier.weight(1.5f),
                                        onClick = onMediaPanelClick,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = brandPurple)
                                    ) {
                                        Icon(Icons.Default.Visibility, null, Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Se Panel", fontSize = 12.sp)
                                    }

                                    FilledTonalButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onEditPointClick,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, null, Modifier.size(16.dp))
                                    }

                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onEditRouteClick,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Route, null, Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // --- DE FLYTENDE KNAPPENE (Ligger fast nederst) ---
                ExtendedFloatingActionButton(
                    onClick = onAddMediaTextClick,
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.TextFields, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Legg til tekst")
                }

                FloatingActionButton(
                    onClick = onCreatePointClick,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = brandPurple,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(28.dp))
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize().background(backgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Ingen opplevelsespunkt enda")
            }
        }
    }
}