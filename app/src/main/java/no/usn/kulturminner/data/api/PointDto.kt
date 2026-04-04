package no.usn.kulturminner.data.api

import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val id: String? = null,
    val title: String,
    val lat: Double,
    val lng: Double,
    val radius: Int,
    val audioUrl: String? = null,
    val sections: List<SectionDto> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)