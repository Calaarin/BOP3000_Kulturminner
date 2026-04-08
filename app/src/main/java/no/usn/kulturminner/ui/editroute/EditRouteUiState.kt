package no.usn.kulturminner.ui.editroute

data class EditRouteUiState(
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)