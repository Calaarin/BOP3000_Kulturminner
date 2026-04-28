package no.usn.kulturminner.data.model

// En enkelt "seksjon" i mediapanelet
data class Section(
    val id: String? = null,                         // Unik ID for hver seksjon settes i server
    val heading: String? = null,                    // Valgfri overskrift
    val text: String? = null,                       // Valgfri avsnitt med tekst
    val imageUrl: String? = null,                   // Valgfri bilde
    val videoUrl: String? = null                    // Valgfri video
)
