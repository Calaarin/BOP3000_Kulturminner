package no.usn.kulturminner.ui.explore

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import androidx.compose.ui.platform.LocalContext
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.maplibre.android.maps.MapLibreMap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Route
import no.usn.kulturminner.ui.components.BaseMap
import no.usn.kulturminner.ui.utils.toGeoJson
import no.usn.kulturminner.R
import no.usn.kulturminner.data.local.TokenStorage
import org.maplibre.android.camera.CameraUpdateFactory

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    tokenStorage: TokenStorage,
    onToggleSimulationPause: () -> Unit,
    onIncreaseSpeed: () -> Unit,
    onDecreaseSpeed: () -> Unit,
    onToggleLocationMode: () -> Unit
) {
    val context = LocalContext.current
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }
    // Posisjonstillatelser fra bruker
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        // når granted er true starter posisjon automatisk via ViewModel
    }

    // Lauchedeffekt til å spørre bruker om tillatelse til henting av enhetens posisjon
    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ======================================== KART ========================================

        // Kart fyller hele skjermen i bakgrunnen
        BaseMap(
            modifier = Modifier.fillMaxSize(),
            initialLat = 59.41,
            initialLng = 9.0588,
            initialZoom = 15.0,
            onMapReady = { map ->
                mapRef = map
                val style = map.style ?: return@BaseMap

                // 1. Last inn ikon-bitmap fra drawable
                val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_location_on)!!
                val iconBitmap = drawable.intrinsicWidth.let { w ->
                    drawable.intrinsicHeight.let { h ->
                        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    }
                }
                val canvas = Canvas(iconBitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                style.addImage("punkt-ikon", iconBitmap)

                // 2. Rute-lag (LineLayer) – legges til først, havner under punkter
                style.addSource(GeoJsonSource("ruter-source", uiState.routes.toGeoJson()))
                style.addLayer(
                    LineLayer("ruter-lag", "ruter-source").withProperties(
                        PropertyFactory.lineColor("#8B1A1A"),
                        PropertyFactory.lineWidth(3f),
                        PropertyFactory.lineOpacity(0.8f)
                    )
                )

                // 3. Punkt-lag (SymbolLayer) – legges til etter ruter, havner over
                style.addSource(GeoJsonSource("punkter-source", uiState.points.toGeoJson()))
                style.addLayer(
                    SymbolLayer("punkter-lag", "punkter-source").withProperties(
                        PropertyFactory.iconImage("punkt-ikon"),
                        PropertyFactory.iconSize(1.4f),
                        PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                        PropertyFactory.iconAllowOverlap(true)
                    )
                )

                // Brukerposisjon (sjekker permission først)
                val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    val locationComponent = map.locationComponent
                    locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(context, style)
                            .useDefaultLocationEngine(true)
                            .build()
                    )
                    locationComponent.isLocationComponentEnabled = true
                    locationComponent.cameraMode = CameraMode.TRACKING
                    locationComponent.renderMode = RenderMode.COMPASS
                }

                // Last inn simulert bruker-ikon
                val userDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_user_simulated)!!
                val userBitmap = Bitmap.createBitmap(
                    userDrawable.intrinsicWidth,
                    userDrawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                Canvas(userBitmap).also { canvas ->
                    userDrawable.setBounds(0, 0, canvas.width, canvas.height)
                    userDrawable.draw(canvas)
                }
                style.addImage("user-ikon", userBitmap)

                // Simulert bruker som GeoJSON-punkt
                                val userGeoJson = """
                    {"type":"FeatureCollection","features":[
                        {"type":"Feature",
                         "geometry":{"type":"Point",
                             "coordinates":[${uiState.simulatedLng},${uiState.simulatedLat}]},
                         "properties":{}}
                    ]}
                """.trimIndent()

                style.addSource(GeoJsonSource("user-source", userGeoJson))
                style.addLayer(
                    SymbolLayer("user-lag", "user-source").withProperties(
                        PropertyFactory.iconImage("user-ikon"),
                        PropertyFactory.iconSize(1.5f),
                        PropertyFactory.iconAllowOverlap(true)
                    )
                )
            }
        )

        // Bruker LaunchedEffect til å legge til (oppdatere) punkter i kartet når de er hentet fra database
        LaunchedEffect(uiState.points) {
            val map = mapRef ?: return@LaunchedEffect
            val style = map.style ?: return@LaunchedEffect
            val source = style.getSourceAs<GeoJsonSource>("punkter-source") ?: return@LaunchedEffect
            source.setGeoJson(uiState.points.toGeoJson())
        }

        // Samme bruk av LaunchedEffect for å legge til ruter i kart når de har blitt hentet fra oppdragsgivers server
        LaunchedEffect(uiState.routes) {
            val map = mapRef ?: return@LaunchedEffect
            val style = map.style ?: return@LaunchedEffect
            val source = style.getSourceAs<GeoJsonSource>("ruter-source") ?: return@LaunchedEffect
            source.setGeoJson(uiState.routes.toGeoJson())
        }

        // Oppdaterer simulert brukerposisjon når den endres
        LaunchedEffect(uiState.simulatedLat, uiState.simulatedLng) {
            val map = mapRef ?: return@LaunchedEffect
            val style = map.style ?: return@LaunchedEffect
            val source = style.getSourceAs<GeoJsonSource>("user-source") ?: return@LaunchedEffect
            val userGeoJson = """
                {"type":"FeatureCollection","features":[
                    {"type":"Feature",
                     "geometry":{"type":"Point",
                         "coordinates":[${uiState.simulatedLng},${uiState.simulatedLat}]},
                     "properties":{}}
                ]}
            """.trimIndent()
                    source.setGeoJson(userGeoJson)
        }

        LaunchedEffect(uiState.simulatedLat, uiState.simulatedLng) {
            if (!uiState.isUsingSimulation) return@LaunchedEffect  //
            val map = mapRef ?: return@LaunchedEffect
            if (!uiState.isSimulationPaused) {
                map.animateCamera(
                    CameraUpdateFactory.newLatLng(
                        org.maplibre.android.geometry.LatLng(
                            uiState.simulatedLat,
                            uiState.simulatedLng
                        )
                    )
                )
            }
        }

        // Oppdatering av synlighet på simulert bruker-ikon avhengig om man simulerer eller ikke
        LaunchedEffect(uiState.isUsingSimulation) {
            val map = mapRef ?: return@LaunchedEffect
            val style = map.style ?: return@LaunchedEffect
            val layer = style.getLayerAs<SymbolLayer>("user-lag") ?: return@LaunchedEffect
            layer.setProperties(
                PropertyFactory.visibility(
                    if (uiState.isUsingSimulation) Property.VISIBLE else Property.NONE
                )
            )
        }

        // Pause- og start-knapp til å sette simulert brukerbevegelse på pause
        if (tokenStorage.isLoggedIn()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle-knapp blir alltid synlig når admin er logget inn
                FloatingActionButton(
                    onClick = onToggleLocationMode,
                    modifier = Modifier.size(48.dp),
                    containerColor = if (uiState.isUsingSimulation) Color(0xFF2E7D32) else Color(0xFF6F63D9),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (uiState.isUsingSimulation)
                            Icons.Default.Route
                        else
                            Icons.Default.MyLocation,
                        contentDescription = if (uiState.isUsingSimulation) "Bytt til ekte GPS" else "Bytt til simulering"
                    )
                }

                // Fart-kontroller kun ved simulering
                if (uiState.isUsingSimulation) {
                    FloatingActionButton(
                        onClick = onDecreaseSpeed,
                        modifier = Modifier.size(48.dp),
                        containerColor = Color(0xFF6F63D9),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Reduser fart")
                    }

                    FloatingActionButton(
                        onClick = onToggleSimulationPause,
                        modifier = Modifier.size(48.dp),
                        containerColor = Color(0xFF6F63D9),
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = if (uiState.isSimulationPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (uiState.isSimulationPaused) "Fortsett" else "Pause"
                        )
                    }

                    FloatingActionButton(
                        onClick = onIncreaseSpeed,
                        modifier = Modifier.size(48.dp),
                        containerColor = Color(0xFF6F63D9),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Øk fart")
                    }
                }
            }
        }

        // ======================================== MEDIAPANEL ========================================

        // Mediapanel vises kun som overlay over kart når bruker er nært et punkt
        AnimatedVisibility(
            visible = uiState.activePoint != null,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }, // starter nedenfor skjermen
                animationSpec = tween(durationMillis = 400, easing = EaseOut)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }, // glir ut nedover
                animationSpec = tween(durationMillis = 300, easing = EaseIn)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.BottomCenter)
        ) {
            MediaPanel(
                uiState = uiState,
                // point = uiState.activePoint, - ikke i bruk nå
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f) // settes til ca 45% av skjermen
                    .align(Alignment.BottomCenter)
            )
        }
    }
}