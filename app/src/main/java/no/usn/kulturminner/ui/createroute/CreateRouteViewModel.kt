package no.usn.kulturminner.ui.createroute

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateRouteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRouteUiState())
    val uiState: StateFlow<CreateRouteUiState> = _uiState.asStateFlow()

}