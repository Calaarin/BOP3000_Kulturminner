package no.usn.kulturminner.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SectionDto(
    val id: String? = null,
    @SerialName("point_id") val pointId: String? = null,
    val heading: String? = null,
    val text: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("video_url") val videoUrl: String? = null
)