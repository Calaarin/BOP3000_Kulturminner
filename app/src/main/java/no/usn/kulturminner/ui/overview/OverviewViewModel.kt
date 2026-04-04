package no.usn.kulturminner.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.repository.`OverviewRepository-dummydata`

class OverviewViewModel(
    private val repository: `OverviewRepository-dummydata` = `OverviewRepository-dummydata`()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    // Holder på "default"-rekkefølge (simulert dato/tid) for sortering
    private var originalPoints: List<String> = emptyList()

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
                    originalPoints = points

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

    // Sorter alfabetisk
    fun sortAlphabetically() {
        val sorted = _uiState.value.demoPoints.sorted()

        _uiState.value = _uiState.value.copy(
            demoPoints = sorted,
            isSortedAlphabetically = true
        )
    }

    // Tilbake til default "dato/tid" sortering
    fun sortByDate() {
        _uiState.value = _uiState.value.copy(
            demoPoints = originalPoints,
            isSortedAlphabetically = false
        )
    }
}