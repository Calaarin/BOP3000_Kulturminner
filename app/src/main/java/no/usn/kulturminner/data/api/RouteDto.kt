package no.usn.kulturminner.data.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import no.usn.kulturminner.data.model.LatLng
import no.usn.kulturminner.data.model.Route

@Serializable
data class GeoJsonFeatureCollection(
    val type: String,
    val features: List<GeoJsonFeature>
)

@Serializable
data class GeoJsonFeature(
    val type: String,
    val geometry: GeoJsonGeometry,
    val properties: JsonObject? = null
)

@Serializable
data class GeoJsonGeometry(
    val type: String,
    val coordinates: List<List<Double>>  // [lng, lat] per punkt
)

fun GeoJsonFeatureCollection.toRoutes(): List<Route> =
    features.mapIndexed { index, feature ->
        Route(
            id = "rute-$index",
            coordinates = feature.geometry.coordinates.map { coord ->
                LatLng(lat = coord[1], lng = coord[0])  // rekkefølge må byttes: GeoJSON er [lng, lat]
            }
        )
    }