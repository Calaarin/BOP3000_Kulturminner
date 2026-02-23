package no.usn.kulturminner.ui.editroute

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditRouteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditRouteUiState())
    val uiState: StateFlow<EditRouteUiState> = _uiState.asStateFlow()

}