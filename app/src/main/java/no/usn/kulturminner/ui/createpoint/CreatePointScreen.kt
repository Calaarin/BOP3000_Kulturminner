package no.usn.kulturminner.ui.createpoint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.platform.LocalContext
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.filled.Close

import no.usn.kulturminner.R
import no.usn.kulturminner.ui.components.BaseMap
import no.usn.kulturminner.ui.components.FormLabel
import no.usn.kulturminner.ui.components.SectionHeader
import no.usn.kulturminner.ui.components.SmallInputField
import no.usn.kulturminner.ui.components.LargeInputField
import no.usn.kulturminner.ui.components.MessageDialog
import no.usn.kulturminner.ui.components.SavingDialog
import no.usn.kulturminner.ui.components.UploadButton

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun CreatePointScreen(
    uiState: CreatePointUiState,
    onTitleChange: (String) -> Unit,
    onLatChange: (Double) -> Unit,
    onLngChange: (Double) -> Unit,
    onRadiusChange: (String) -> Unit,
    onAudioUrlChange: (String) -> Unit,
    onUpdateSection: (Int, SectionUiState) -> Unit,
    onExpandSectionCountDropdown: () -> Unit,
    onDismissSectionCountDropdown: () -> Unit,
    onSectionCountChange: (Int) -> Unit,

    onImageSelected: (Int, Uri) -> Unit,
    onVideoSelected: (Int, Uri) -> Unit,
    onAudioSelected: (Uri) -> Unit,
    onRemoveImage: (Int) -> Unit,
    onRemoveVideo: (Int) -> Unit,
    onRemoveAudio: () -> Unit,

    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDismissPopup: () -> Unit
) {
    val backgroundColor = Color(0xFFF4F1F8)
    val buttonBackgroundColor = Color(0xFFD3D1E9)
    val panelColor = Color(0xFFE7E7E7)
    val sectionHeaderColor = Color(0xFFD8D0F6)
    val uploadBorderColor = Color(0xFF4F46A3)      // mørkere lilla
    val uploadBackgroundColor = Color(0xFFDEDDE6)  // lys grå-lilla bakgrunn

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    // Laucher for lydopplasting
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onAudioSelected(it) }
    }

    // Start av UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Popup med melding ved lagring av punkt
        if (uiState.isSaving) {
            SavingDialog(message = "Oppretter punkt...")
        }

        // Feilmelding for feil format i radius tekstfelt
        uiState.popupMessage?.let { message ->
            MessageDialog(
                message = message,
                onDismiss = onDismissPopup   // nullstilling av popupMessage
            )
        }

        // Kart til plassering av punkt
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            BaseMap(
                modifier = Modifier.fillMaxSize(),
                initialLat = 59.41,
                initialLng = 9.06,
                initialZoom = 12.0,
                onMapReady = { map ->
                    val style = map.style ?: return@BaseMap

                    // Ikon for valgt punkt
                    val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_location_on)!!
                    val bitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    Canvas(bitmap).also {
                        drawable.setBounds(0, 0, it.width, it.height)
                        drawable.draw(it)
                    }
                    style.addImage("valgt-punkt-ikon", bitmap)

                    // Tom kilde til å begynne med
                    style.addSource(GeoJsonSource("valgt-punkt-source",
                        """{"type":"FeatureCollection","features":[]}"""))
                    style.addLayer(
                        SymbolLayer("valgt-punkt-lag", "valgt-punkt-source").withProperties(
                            PropertyFactory.iconImage("valgt-punkt-ikon"),
                            PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                            PropertyFactory.iconAllowOverlap(true)
                        )
                    )

                    // Klikk setter markør og oppdaterer UiState
                    map.addOnMapClickListener { latLng ->
                        onLatChange(latLng.latitude)
                        onLngChange(latLng.longitude)

                        // Oppdater markør-posisjonen
                        val source = style.getSourceAs<GeoJsonSource>("valgt-punkt-source")
                        source?.setGeoJson("""
                        {"type":"FeatureCollection","features":[
                            {"type":"Feature",
                             "geometry":{"type":"Point",
                                 "coordinates":[${latLng.longitude},${latLng.latitude}]},
                             "properties":{}}
                        ]}
                    """.trimIndent())
                        true
                    }
                }
            )

            // Overlay-tekst når ingen koordinat er valgt ennå
            if (uiState.lat == 0.0 && uiState.lng == 0.0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 150.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF6F63D9)
                ) {
                    Text(
                        text = "Trykk i kartet for å sette punkt",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color.White
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp)  // setter bare statisk visuell høyde av kartdelen for nå
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)  // plass til bunnpanel
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(panelColor)
                    .padding(16.dp),
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
                    value = uiState.radius,
                    onValueChange = onRadiusChange,
                    placeholder = "F.eks 50"
                )

                FormLabel("LYDFIL")
                SmallInputField(
                    value = uiState.audioUrl,
                    onValueChange = { if (!uiState.isAudioUploaded) onAudioUrlChange(it) },
                    placeholder = "Lim inn URL eller last opp fil",
                    readOnly = uiState.isAudioUploaded,
                    textColor = if (uiState.isAudioUploaded) Color(0xFF2E7D32) else Color.Black,
                    trailingIcon = if (uiState.isAudioUploaded) {
                        {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .clickable { onRemoveAudio() }
                                    .padding(end = 18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.Gray
                                )
                                Text(
                                    text = "Fjern",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else null
                )

                UploadButton(
                    text = "Last opp fil",
                    borderColor = uploadBorderColor,
                    onClick = { audioPickerLauncher.launch(arrayOf("audio/mpeg", "audio/wav")) }
                )

                Text(
                    text = "Støttede formater: mp3, wav",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dynamisk antall seksjoner
                FormLabel("ANTALL SEKSJONER")

                Box {
                    OutlinedButton(
                        onClick = onExpandSectionCountDropdown,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val text = if (uiState.selectedSectionCount == 1) {
                                "1 seksjon"
                            } else {
                                "${uiState.selectedSectionCount} seksjoner"
                            }

                            Text(text = text, color = Color.Black)
                            Icon(
                                imageVector = Icons.Outlined.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = uiState.isSectionCountDropdownExpanded,
                        onDismissRequest = onDismissSectionCountDropdown
                    ) {
                        (1..5).forEach { number ->
                            val itemText = if (number == 1) "1 seksjon" else "$number seksjoner"

                            DropdownMenuItem(
                                text = { Text(itemText) },
                                onClick = { onSectionCountChange(number) }
                            )
                        }
                    }
                }

                // Dynamiske seksjoner
                uiState.sections.forEachIndexed { index, section ->

                    // Laucher til bildeopplasting
                    val imagePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        uri?.let { onImageSelected(index, it) }  // ← index herfra
                    }
                    // Launcher til videoopplasting
                    val videoPickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        uri?.let { onVideoSelected(index, it) }
                    }

                    SectionHeader("Seksjon ${index + 1}")

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
                        onValueChange = { if (!section.isImageUploaded) onUpdateSection(index, section.copy(imageUrl = it)) },
                        placeholder = "Lim inn URL eller last opp fil",
                        readOnly = section.isImageUploaded,
                        textColor = if (section.isImageUploaded) Color(0xFF2E7D32) else Color.Black,
                        trailingIcon = if (section.isImageUploaded) {
                            {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier
                                        .clickable { onRemoveImage(index) }
                                        .padding(end = 18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.Gray
                                    )
                                    Text(
                                        text = "Fjern",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else null
                    )

                    // Bilde-opplastingsknapp
                    UploadButton(
                        text = "Last opp fil",
                        borderColor = uploadBorderColor,
                        onClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    Text(
                        text = "Støttede formater: jpg, jpeg, png",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FormLabel("VIDEO")
                    SmallInputField(
                        value = section.videoUrl,
                        onValueChange = { if (!section.isVideoUploaded) onUpdateSection(index, section.copy(videoUrl = it)) },
                        placeholder = "Lim inn URL eller last opp fil",
                        readOnly = section.isVideoUploaded,
                        textColor = if (section.isVideoUploaded) Color(0xFF2E7D32) else Color.Black,
                        trailingIcon = if (section.isVideoUploaded) {
                            {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier
                                        .clickable { onRemoveVideo(index) }
                                        .padding(end = 18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color.Gray
                                    )
                                    Text(
                                        text = "Fjern",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else null
                    )

                    // Video-opplastingsknapp
                    UploadButton(
                        text = "Last opp fil",
                        borderColor = uploadBorderColor,
                        onClick = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                    )

                    Text(
                        text = "Støttede formater: mp4",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(38.dp))
            }
        }

        // Bunnpanel med knapper - animeres ut når tastatur er oppe
        AnimatedVisibility(
            visible = !isKeyboardVisible,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
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
                        Text("Lagre punkt")
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
}