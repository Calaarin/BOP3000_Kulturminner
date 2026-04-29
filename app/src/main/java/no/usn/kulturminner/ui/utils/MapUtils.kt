package no.usn.kulturminner.ui.utils

import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.model.Route

@JvmName("pointListToGeoJson")
fun List<Point>.toGeoJson(): String {
    val features = joinToString(",") { point ->
        """
        {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [${point.lng}, ${point.lat}]
            },
            "properties": {
                "id": "${point.id ?: ""}",
                "title": "${point.title}"
            }
        }
        """.trimIndent()
    }
    return """{"type":"FeatureCollection","features":[$features]}"""
}

@JvmName("routeListToGeoJson")
fun List<Route>.toGeoJson(): String {
    val features = joinToString(",") { route ->
        val coords = route.coordinates.joinToString(",") { "[${it.lng},${it.lat}]" }
        """
        {
            "type": "Feature",
            "geometry": {
                "type": "LineString",
                "coordinates": [$coords]
            },
            "properties": {
                "id": "${route.id}"
            }
        }
        """.trimIndent()
    }
    return """{"type":"FeatureCollection","features":[$features]}"""
}