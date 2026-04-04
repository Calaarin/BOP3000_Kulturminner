package no.usn.kulturminner.data.api

import kotlinx.serialization.Serializable

@Serializable
data class SectionDto(
    val id: String? = null,
    val heading: String? = null,
    val text: String? = null,
    val imageUrl: String? = null,
    val videoUrl: String? = null
)