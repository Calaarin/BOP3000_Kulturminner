package no.usn.kulturminner.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val id: String? = null,
    val title: String,
    val lat: Double,
    val lng: Double,
    val radius: Int,
    @SerialName("audio_url") val audioUrl: String? = null,
    val sections: List<SectionDto> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("user_id") val userId: String? = null
)