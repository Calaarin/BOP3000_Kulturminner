package no.usn.kulturminner.ui.mediapanel

// Data-klassen fra AI-en
data class TextSection(
    val title: String? = null,
    val content: String? = null,
    val columnCount: Int = 1
)

data class MediaPanelUiState(
    val sections: List<TextSection> = emptyList(),
    val isLoading: Boolean = false
)