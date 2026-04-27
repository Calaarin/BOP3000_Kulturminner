package no.usn.kulturminner.ui.editpoint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.FileUpload // Fjern
import androidx.compose.material.icons.outlined.LocationOn // Fjern
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.platform.LocalContext
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraPosition

import no.usn.kulturminner.R
import no.usn.kulturminner.ui.components.BaseMap
import no.usn.kulturminner.ui.components.FormLabel
import no.usn.kulturminner.ui.components.SectionHeader
import no.usn.kulturminner.ui.components.SmallInputField
import no.usn.kulturminner.ui.components.LargeInputField
import no.usn.kulturminner.ui.components.UploadButton


@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun EditPointScreen(
    uiState: EditPointUiState,
    onTitleChange: (String) -> Unit,
    onLatChange: (Double) -> Unit,
    onLngChange: (Double) -> Unit,
    onRadiusChange: (String) -> Unit,
    onAudioUrlChange: (String) -> Unit,
    onUpdateSection: (Int, SectionUiState) -> Unit,
    onExpandSectionCountDropdown: () -> Unit,
    onDismissSectionCountDropdown: () -> Unit,
    onSectionCountChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
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
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }

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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                // Kart til plassering av punkt so første item
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    BaseMap(
                        modifier = Modifier.fillMaxSize(),
                        initialLat = 59.41,  // fallback mens data lastes
                        initialLng = 9.06,   // fallback mens data lastes
                        initialZoom = 14.0,          // litt nærmere zoomet inn enn CreatePointScreen
                        onMapReady = { map ->
                            mapRef = map
                            val style = map.style ?: return@BaseMap

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

                            // Eksisterende koordinat som startmarkør – ikke tom som i CreatePoint
                            val startGeoJson = if (uiState.lat != 0.0 && uiState.lng != 0.0) {
                                """{"type":"FeatureCollection","features":[
                                    {"type":"Feature",
                                     "geometry":{"type":"Point",
                                         "coordinates":[${uiState.lng},${uiState.lat}]},
                                     "properties":{}}
                                ]}"""
                            } else {
                                """{"type":"FeatureCollection","features":[]}"""
                            }

                            style.addSource(GeoJsonSource("valgt-punkt-source", startGeoJson))
                            style.addLayer(
                                SymbolLayer("valgt-punkt-lag", "valgt-punkt-source").withProperties(
                                    PropertyFactory.iconImage("valgt-punkt-ikon"),
                                    PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                                    PropertyFactory.iconAllowOverlap(true)
                                )
                            )

                            // Klikk oppdaterer posisjon – samme som CreatePoint
                            map.addOnMapClickListener { latLng ->
                                onLatChange(latLng.latitude)
                                onLngChange(latLng.longitude)
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

                    // Oppdater markør hvis koordinatene endres i uiState (f.eks. ved dataloading)
                    LaunchedEffect(uiState.lat, uiState.lng, mapRef) {
                        val map = mapRef ?: return@LaunchedEffect
                        val style = map.style ?: return@LaunchedEffect
                        val source = style.getSourceAs<GeoJsonSource>("valgt-punkt-source") ?: return@LaunchedEffect

                        if (uiState.lat != 0.0 && uiState.lng != 0.0) {
                            source.setGeoJson("""
                                {"type":"FeatureCollection","features":[
                                    {"type":"Feature",
                                     "geometry":{"type":"Point",
                                         "coordinates":[${uiState.lng},${uiState.lat}]},
                                     "properties":{}}
                                ]}
                            """.trimIndent())

                            // Flytt kamera til punktets koordinater
                            map.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder()
                                        .target(LatLng(uiState.lat, uiState.lng))
                                        .zoom(14.0)
                                        .build()
                                )
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 220.dp)  // visuell høyde av kartdelen
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
                            onValueChange = onAudioUrlChange,
                            placeholder = "Lim inn URL eller last opp fil"
                        )

                        UploadButton(text = "Last opp fil", borderColor = uploadBorderColor)

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
                                onValueChange = { onUpdateSection(index, section.copy(imageUrl = it)) },
                                placeholder = "Lim inn URL eller last opp fil"
                            )
                            UploadButton(text = "Last opp fil", borderColor = uploadBorderColor)

                            Text(
                                text = "Støttede formater: jpg, jpeg, png",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            FormLabel("VIDEO")
                            SmallInputField(
                                value = section.videoUrl,
                                onValueChange = { onUpdateSection(index, section.copy(videoUrl = it)) },
                                placeholder = "Lim inn URL eller last opp fil"
                            )
                            UploadButton(text = "Last opp fil", borderColor = uploadBorderColor)

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

// Hjelpekomposables (samme som i CreatePointScreen)
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
            .height(154.dp),
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(8.dp),
        maxLines = 5
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.FileUpload,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = borderColor
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(text = text, color = borderColor)
    }
}