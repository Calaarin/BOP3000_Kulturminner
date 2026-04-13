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
import no.usn.kulturminner.ui.components.BaseMap
import no.usn.kulturminner.ui.utils.toGeoJson
import no.usn.kulturminner.R
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources

@Composable
fun ExploreScreen(
    uiState: ExploreUiState
) {
    val context = LocalContext.current
    // Posisjonstillatelser fra bruker
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        // når granted er true starter posisjon automatisk via ViewModel
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Kart i bakgrunnen – fyller hele skjermen
        BaseMap(
            modifier = Modifier.fillMaxSize(),
            initialLat = 59.41,
            initialLng = 9.06,
            initialZoom = 12.0,
            onMapReady = { map ->
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
                        PropertyFactory.lineColor("#8B4513"),
                        PropertyFactory.lineWidth(3f),
                        PropertyFactory.lineOpacity(0.8f)
                    )
                )

                // 3. Punkt-lag (SymbolLayer) – legges til sist, havner øverst
                style.addSource(GeoJsonSource("punkter-source", uiState.points.toGeoJson()))
                style.addLayer(
                    SymbolLayer("punkter-lag", "punkter-source").withProperties(
                        PropertyFactory.iconImage("punkt-ikon"),
                        PropertyFactory.iconSize(1.4f),
                        PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                        PropertyFactory.iconAllowOverlap(true)
                    )
                )

                // Brukerposisjon – sjekk permission først
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
            }
        )

        // Mediapanel som overlay nederst – kun synlig når bruker er nær punkt
        // (foreløpig alltid synlig for testing)
        MediaPanel(
            uiState = uiState,
            point = uiState.activePoint,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f) // ca 45% av skjermen
                .align(Alignment.BottomCenter)
        )
    }
}