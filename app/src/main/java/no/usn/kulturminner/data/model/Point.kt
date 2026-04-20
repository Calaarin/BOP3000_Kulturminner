package no.usn.kulturminner.data.model

import java.time.Instant

// Hovedklasse for et opplevelsespunkt
data class Point(
    val id: String? = null,                         // Unik ID for hvert punkt settes i server
    val userId: String? = null,                     // Midlertidig brukerid før login er på plass (fremmednøkkel mot brukertabell)
    val title: String,                              // Hovedtittel for hele punktet
    val lat: Double,
    val lng: Double,
    val radius: Int,                                // Søkeradius i meter
    val audioUrl: String? = null,                   // Valgfri lydfil med fortellerstemme
    val sections: List<Section> = emptyList(),      // Liste med seksjoner

    // Timestamps - disse skal settes fra server, ikke klient
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)

