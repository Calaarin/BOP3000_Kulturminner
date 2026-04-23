package no.usn.kulturminner.ui.components

import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.ExperimentalComposeUiApi
import org.maplibre.android.maps.MapLibreMap // Sjekk om dette er riktig import

private const val KARTVERKET_TOPO_URL =
    "https://cache.kartverket.no/v1/wmts/1.0.0/topo/default/webmercator/{z}/{y}/{x}.png"

private fun kartverketStyle(): String = """
{
  "version": 8,
  "sources": {
    "kartverket-topo": {
      "type": "raster",
      "tiles": ["$KARTVERKET_TOPO_URL"],
      "tileSize": 256,
      "attribution": "© Kartverket"
    }
  },
  "layers": [
    {
      "id": "kartverket-topo-layer",
      "type": "raster",
      "source": "kartverket-topo"
    }
  ]
}
""".trimIndent()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BaseMap(
    modifier: Modifier = Modifier,
    initialLat: Double = 59.41,
    initialLng: Double = 9.061,
    initialZoom: Double = 12.0,
    onMapReady: (MapLibreMap) -> Unit = {}   // kart-objektet sendes ut (MapLibreMap import i bruk her)
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    remember { MapLibre.getInstance(context) }

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { map ->
                map.setStyle(Style.Builder().fromJson(kartverketStyle())) {
                    // Style er lastet – nå er det trygt å legge til lag/symboler
                    onMapReady(map)
                }
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(initialLat, initialLng))
                    .zoom(initialZoom)
                    .build()

                // Eksplisitt aktiver zoom og scroll (er default, men tydeliggjør det)
                map.uiSettings.isZoomGesturesEnabled = true
                map.uiSettings.isScrollGesturesEnabled = true
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE  -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START   -> mapView.onStart()
                Lifecycle.Event.ON_RESUME  -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE   -> mapView.onPause()
                Lifecycle.Event.ON_STOP    -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
            .nestedScroll(object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    return available
                }
            })
            .pointerInteropFilter { motionEvent ->
                mapView.dispatchTouchEvent(motionEvent)
                true // true = hendelsen er konsumert, bobler ikke videre
            }
    )
}