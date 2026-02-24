package no.usn.kulturminner.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.repository.OverviewRepository

class OverviewViewModel(
    private val repository: OverviewRepository = OverviewRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init {
        fetchOverview()
    }

    private fun fetchOverview() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            repository.getDemoPoints()
                .onSuccess { points ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        demoPoints = points
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
        }
    }
}