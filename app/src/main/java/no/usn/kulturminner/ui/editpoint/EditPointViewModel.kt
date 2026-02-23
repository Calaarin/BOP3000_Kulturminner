package no.usn.kulturminner.ui.editpoint

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditPointViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditPointUiState())
    val uiState: StateFlow<EditPointUiState> = _uiState.asStateFlow()

}