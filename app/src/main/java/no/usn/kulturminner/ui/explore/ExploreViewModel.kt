package no.usn.kulturminner.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.repository.PointRepository

class ExploreViewModel(
    private val pointRepository: PointRepository    // routeRepository legges til senere
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAllPoints()
    }

    fun fetchAllPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(arePointsLoading = true, pointError = null) }

            pointRepository.getDummyPoints()           // Bytt til getAllPoints() når vi skal hente data fra server
                .onSuccess { points ->
                    _uiState.update { it.copy(points = points, arePointsLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(pointError = e.message, arePointsLoading = false) }
                }
        }
    }
}