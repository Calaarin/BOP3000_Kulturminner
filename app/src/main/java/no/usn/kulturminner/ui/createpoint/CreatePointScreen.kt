package no.usn.kulturminner.ui.createpoint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CreatePointScreen(
    uiState: CreatePointUiState,
    onTitleChange: (String) -> Unit,
    onLatChange: (Double) -> Unit,
    onLngChange: (Double) -> Unit,
    onRadiusChange: (Int) -> Unit,
    onAudioUrlChange: (String) -> Unit,
    onUpdateSection: (Int, SectionUiState) -> Unit,
    onAddSection: () -> Unit,
    onRemoveSection: (Int) -> Unit,
    onSectionCountChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val backgroundColor = Color(0xFFF4F1F8)
    val panelColor = Color(0xFFE7E7E7)
    val sectionHeaderColor = Color(0xFFD8D0F6)
    val uploadBorderColor = Color(0xFF8E7AE6)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Kart-plassholder (senere erstattes med ekte kart)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                color = Color(0xFFE6E6E6)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFF7AA8F8)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp).size(28.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { /* TODO: Åpne kart for å sette punkt */ },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6F63D9))
                        ) {
                            Text("Trykk for å sette punkt")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = panelColor)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FormLabel("TITTEL")
                    SmallInputField(
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        placeholder = "Navn på opplevelsespunkt"
                    )

                    FormLabel("RADIUS (meter)")
                    SmallInputField(
                        value = uiState.radius.toString(),
                        onValueChange = { onRadiusChange(it.toIntOrNull() ?: 50) },
                        placeholder = "F.eks 50"
                    )

                    FormLabel("LYDFIL")
                    SmallInputField(
                        value = uiState.audioUrl,
                        onValueChange = onAudioUrlChange,
                        placeholder = "Lim inn URL eller last opp fil"
                    )

                    UploadButton(text = "Last opp fil", borderColor = uploadBorderColor)

                    Text(
                        text = "Støttende formater: mp3, wav",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dynamisk antall seksjoner
                    FormLabel("ANTALL SEKSJONER")

                    Box {
                        OutlinedButton(
                            onClick = { uiState.sectionsExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${uiState.selectedSectionCount} seksjoner",
                                    color = Color.Black
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = uiState.sectionsExpanded,
                            onDismissRequest = { uiState.sectionsExpanded = false }
                        ) {
                            (1..5).forEach { number ->
                                DropdownMenuItem(
                                    text = { Text("$number seksjoner") },
                                    onClick = {
                                        onSectionCountChange(number)      // callback til ViewModel
                                        uiState.sectionsExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Dynamiske seksjoner
                    uiState.sections.forEachIndexed { index, section ->
                        SectionHeader("Seksjon ${index + 1}", sectionHeaderColor)

                        FormLabel("OVERSKRIFT")
                        SmallInputField(
                            value = section.heading,
                            onValueChange = { onUpdateSection(index, section.copy(heading = it)) },
                            placeholder = "Tittel for seksjon"
                        )

                        FormLabel("TEKSTAVSNITT")
                        LargeInputField(
                            value = section.text,
                            onValueChange = { onUpdateSection(index, section.copy(text = it)) },
                            placeholder = "Skriv inn innholdstekst..."
                        )

                        FormLabel("BILDE")
                        SmallInputField(
                            value = section.imageUrl,
                            onValueChange = { onUpdateSection(index, section.copy(imageUrl = it)) },
                            placeholder = "Lim inn URL eller last opp fil"
                        )
                        UploadButton(text = "Last opp fil", borderColor = uploadBorderColor)

                        FormLabel("VIDEO")
                        SmallInputField(
                            value = section.videoUrl,
                            onValueChange = { onUpdateSection(index, section.copy(videoUrl = it)) },
                            placeholder = "Lim inn URL eller last opp fil"
                        )
                        UploadButton(text = "Last opp fil", borderColor = uploadBorderColor)
                    }
                }
            }
        }

        // Bunnpanel med knapper
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding(),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            color = backgroundColor
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Lagre punkt")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE25C5C)),
                    border = BorderStroke(1.dp, Color(0xFFF1C5C5))
                ) {
                    Text("Avbryt")
                }
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF5C5C5C)
    )
}

@Composable
private fun SectionHeader(text: String, backgroundColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6F63D9)
        )
    }
}

@Composable
private fun SmallInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun LargeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(8.dp),
        maxLines = 4
    )
}

@Composable
private fun UploadButton(
    text: String,
    borderColor: Color
) {
    OutlinedButton(
        onClick = { },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 12.dp,
            vertical = 0.dp
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.FileUpload,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = borderColor
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = borderColor
        )
    }
}