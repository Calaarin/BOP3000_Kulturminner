package no.usn.kulturminner.ui.mediapanel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaPanelViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MediaPanelUiState())
    val uiState: StateFlow<MediaPanelUiState> = _uiState.asStateFlow()

    init {
        // Laster inn demo-seksjonene fra AI-forslaget
        _uiState.value = MediaPanelUiState(
            sections = listOf(
                TextSection(
                    title = "Historikk",
                    content = "Dette kulturminnet stammer fra vikingtiden og ble oppdaget i 1923.",
                    columnCount = 1
                ),
                TextSection(
                    title = "Beskrivelse",
                    content = "Ruinen består av to fløyer. Den nordlige fløyen er best bevart, mens den sørlige ble delvis restaurert på 1980-tallet.",
                    columnCount = 2
                ),
                TextSection(
                    title = null, // Vises ikke
                    content = "Stedet er fredet etter kulturminneloven § 4.",
                    columnCount = 1
                )
            )
        )
    }
}