package no.usn.kulturminner.data.model

// Hovedklasse for rute
data class Route(
    val id: String,
    val coordinates: List<LatLng>
)