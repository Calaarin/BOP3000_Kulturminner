package no.usn.kulturminner.data.model

import java.time.Instant
import java.util.UUID

// Hovedklasse for et opplevelsespunkt
data class Point(
    val id: String,
    val title: String,                              // Hovedtittel for hele punktet
    val lat: Double,
    val lng: Double,
    val radius: Int,                                // Søkeradius i meter
    val audioUrl: String? = null,                   // Valgfri lydfil med fortellerstemme
    val sections: List<Section> = emptyList(),       // Liste med seksjoner

    // Timestamps - disse skal settes automatisk ved lagring
    val createdAt: Instant,
    val updatedAt: Instant = createdAt              // Ved opprettelse er de like
    )

// En enkelt "seksjon" i mediapanelet
data class Section(
    val id: String = UUID.randomUUID().toString(),   // Unik ID for hver seksjon
    val heading: String? = null,                    // Valgfri overskrift
    val text: String? = null,                       // Valgfri avsnitt med tekst
    val imageUrl: String? = null,                   // Valgfri bilde
    val videoUrl: String? = null                    // Valgfri video
) {
    // Enkel validering: minst ett felt må være fylt
    init {
        require(heading != null || text != null || imageUrl != null || videoUrl != null) {
            "En seksjon må ha minst ett innholdselement"
        }
    }
}