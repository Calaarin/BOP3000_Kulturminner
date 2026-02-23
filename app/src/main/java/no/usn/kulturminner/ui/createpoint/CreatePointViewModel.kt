package no.usn.kulturminner.ui.createpoint

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreatePointViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePointUiState())
    val uiState: StateFlow<CreatePointUiState> = _uiState.asStateFlow()

}