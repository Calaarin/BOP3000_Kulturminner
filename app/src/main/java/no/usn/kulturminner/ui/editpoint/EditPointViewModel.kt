package no.usn.kulturminner.ui.editpoint

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditPointViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditPointUiState())
    val uiState: StateFlow<EditPointUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun onRadiusChange(newRadius: String) {
        _uiState.value = _uiState.value.copy(radius = newRadius)
    }

    fun onAudioChange(newAudio: String) {
        _uiState.value = _uiState.value.copy(audioUrl = newAudio)
    }

    fun loadPoint() {
        _uiState.value = EditPointUiState(
            title = "Atlantarhavsveien",
            radius = "50",
            audioUrl = "https://example.com/audio.mp3",
            isLoading = false,
            error = null
        )
    }

    fun updatePoint() {
        val current = _uiState.value

        println("Saving point:")
        println("Title: ${current.title}")
        println("Radius: ${current.radius}")
        println("Audio: ${current.audioUrl}")
    }
}