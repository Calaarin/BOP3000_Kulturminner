package no.usn.kulturminner.ui.editpoint

data class EditPointUiState(
    // Variabler for feilhåndtering og datahåndtering
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,

    // Punkt-data
    val title: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val radius: Int = 50,
    val audioUrl: String = "",
    val sections: List<SectionUiState> = listOf(SectionUiState())
)

data class SectionUiState(
    val id: String? = null,
    val heading: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val videoUrl: String = ""
)