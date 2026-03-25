package no.usn.kulturminner.data.model

data class Route(
    val id: String,
    val name: String,
    val color: String? = null,
    val coordinates: List<LatLng>
)

data class LatLng(val lat: Double, val lng: Double) // enkel hjelpeklasse