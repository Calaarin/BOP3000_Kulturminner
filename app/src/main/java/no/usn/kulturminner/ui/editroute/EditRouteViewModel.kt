package no.usn.kulturminner.ui.editroute

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditRouteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditRouteUiState())
    val uiState: StateFlow<EditRouteUiState> = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.value = _uiState.value.copy(description = newDescription)
    }

    fun loadRoute() {
        _uiState.value = EditRouteUiState(
            name = "Kyststien",
            description = "En flott rute langs kysten med flere opplevelsespunkter.",
            isLoading = false,
            error = null
        )
    }

    fun updateRoute() {
        val current = _uiState.value

        println("Oppdaterer rute:")
        println("Navn: ${current.name}")
        println("Beskrivelse: ${current.description}")
    }
}