package no.usn.kulturminner.data.model

// Hovedklasse for rute
data class Route(
    val id: String,
    val name: String,
    val color: String? = null,
    val coordinates: List<LatLng>                   // Liste med koordinatpunkt
)

// Enkel hjelpeklasse
data class LatLng(val lat: Double, val lng: Double)