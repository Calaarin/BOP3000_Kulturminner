package no.usn.kulturminner.ui.overview

data class OverviewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val demoPoints: List<String> = emptyList()
)