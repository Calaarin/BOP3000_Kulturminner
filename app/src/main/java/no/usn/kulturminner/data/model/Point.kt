package no.usn.kulturminner.data.model

data class Point(
    val id: String,
    val title: String,
    val description: String?,
    val lat: Double,
    val lng: Double,
    val radius: Int,            // meter
    val mediaType: String,      // "video / "bilde" / "lyd"
    val mediaUrl: String?,
    val imageUrl: String?
)