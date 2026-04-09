package no.usn.kulturminner.ui.createpoint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CreatePointScreenAlt() {
    var title by rememberSaveable { mutableStateOf("") }
    var radius by rememberSaveable { mutableStateOf("") }
    var audioLink by rememberSaveable { mutableStateOf("") }

    var section1Title by rememberSaveable { mutableStateOf("") }
    var section1Text by rememberSaveable { mutableStateOf("") }
    var section1Image by rememberSaveable { mutableStateOf("") }
    var section1Video by rememberSaveable { mutableStateOf("") }

    var section2Title by rememberSaveable { mutableStateOf("") }
    var section2Text by rememberSaveable { mutableStateOf("") }
    var section2Image by rememberSaveable { mutableStateOf("") }
    var section2Video by rememberSaveable { mutableStateOf("") }

    var sectionsExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedSections by rememberSaveable { mutableStateOf("1 seksjoner") }

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
                .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Kartseksjon øverst for å ligne mer på Figma
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFF7AA8F8)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(28.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6F63D9)
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            )
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
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "Navn på opplevelsespunkt"
                    )

                    FormLabel("RADIUS (meter)")
                    SmallInputField(
                        value = radius,
                        onValueChange = { radius = it },
                        placeholder = "F.eks 50"
                    )

                    FormLabel("LYDFIL")
                    SmallInputField(
                        value = audioLink,
                        onValueChange = { audioLink = it },
                        placeholder = "Lim inn URL til lydfil eller last opp fil"
                    )

                    UploadButton(
                        text = "Last opp fil",
                        borderColor = uploadBorderColor
                    )

                    Text(
                        text = "Støttende formater: mp3, wav",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    FormLabel("ANTALL SEKSJONER")

                    // Enkel dropdown som fungerer uten ExposedDropdownMenu
                    Box {
                        OutlinedButton(
                            onClick = { sectionsExpanded = true },
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
                                    text = selectedSections,
                                    color = Color.Black
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = sectionsExpanded,
                            onDismissRequest = { sectionsExpanded = false }
                        ) {
                            listOf("1 seksjoner", "2 seksjoner", "3 seksjoner").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedSections = option
                                        sectionsExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Seksjon 1
                    SectionHeader("Seksjon 1", sectionHeaderColor)

                    FormLabel("OVERSKRIFT")
                    SmallInputField(
                        value = section1Title,
                        onValueChange = { section1Title = it },
                        placeholder = "Tittel for seksjon"
                    )

                    FormLabel("TEKSTAVSNITT")
                    LargeInputField(
                        value = section1Text,
                        onValueChange = { section1Text = it },
                        placeholder = "Skriv inn innholdstekst..."
                    )

                    FormLabel("BILDE")
                    SmallInputField(
                        value = section1Image,
                        onValueChange = { section1Image = it },
                        placeholder = "Lim inn URL til bilde eller last opp fil"
                    )

                    UploadButton(
                        text = "Last opp fil",
                        borderColor = uploadBorderColor
                    )

                    Text(
                        text = "Støttende formater: jpg, jpeg, png",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    FormLabel("VIDEO")
                    SmallInputField(
                        value = section1Video,
                        onValueChange = { section1Video = it },
                        placeholder = "Lim inn URL til video eller last opp fil"
                    )

                    UploadButton(
                        text = "Last opp fil",
                        borderColor = uploadBorderColor
                    )

                    Text(
                        text = "Støttende formater: mp4",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Seksjon 2
                    SectionHeader("Seksjon 2", sectionHeaderColor)

                    FormLabel("OVERSKRIFT")
                    SmallInputField(
                        value = section2Title,
                        onValueChange = { section2Title = it },
                        placeholder = "Tittel for seksjon"
                    )

                    FormLabel("TEKSTAVSNITT")
                    LargeInputField(
                        value = section2Text,
                        onValueChange = { section2Text = it },
                        placeholder = "Skriv inn innholdstekst..."
                    )

                    FormLabel("BILDE")
                    SmallInputField(
                        value = section2Image,
                        onValueChange = { section2Image = it },
                        placeholder = "Lim inn URL til bilde eller last opp fil"
                    )

                    UploadButton(
                        text = "Last opp fil",
                        borderColor = uploadBorderColor
                    )

                    Text(
                        text = "Støttende formater: jpg, jpeg, png",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    FormLabel("VIDEO")
                    SmallInputField(
                        value = section2Video,
                        onValueChange = { section2Video = it },
                        placeholder = "Lim inn URL til video eller last opp fil"
                    )

                    UploadButton(
                        text = "Last opp fil",
                        borderColor = uploadBorderColor
                    )

                    Text(
                        text = "Støttende formater: mp4",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

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
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                // Knapper lagt under hverandre for å ligne bildet mer
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F80ED)
                    )
                ) {
                    Text("Lagre punkt")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE25C5C)
                    ),
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