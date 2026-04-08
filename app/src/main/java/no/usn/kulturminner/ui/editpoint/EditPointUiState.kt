package no.usn.kulturminner.ui.editpoint

data class EditPointUiState(
    val title: String = "",
    val radius: String = "",
    val audioUrl: String = "",

    val isLoading: Boolean = false,
    val error: String? = null
)