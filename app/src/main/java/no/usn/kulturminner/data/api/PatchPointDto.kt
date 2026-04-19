package no.usn.kulturminner.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PointPatchDto(
    val title: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val radius: Int? = null,
    @SerialName("audio_url") val audioUrl: String? = null
    // sections håndteres separat via section-endepunktet
    // id, created_at, updated_at, user_id settes ikke fra klient
)