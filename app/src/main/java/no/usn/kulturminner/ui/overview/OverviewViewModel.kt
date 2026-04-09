package no.usn.kulturminner.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.model.User
import no.usn.kulturminner.data.repository.PointRepository
import no.usn.kulturminner.data.repository.UserRepository

class OverviewViewModel(
    private val userRepository: UserRepository,
    private val pointRepository: PointRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState = _uiState.asStateFlow()

    val userId: String =  "u1"

    init {
        fetchUserData(userId)
        fetchMyPoints()
    }

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUserLoading = true, userError = null) }

            userRepository.getDummyUser(userId)         // Bruk getUser(userId) når serverkommunikasjon er klar
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user, isUserLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(userError = e.message, isUserLoading = false) }
                }
        }
    }

    fun fetchMyPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPointListLoading = true, pointError = null) }

            pointRepository.getDummyPoints()           // Bruk getMyPoints(userId) når serverkommunikasjon er klar
                .onSuccess { points ->
                    _uiState.update { it.copy(points = points, isPointListLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(pointError = e.message, isPointListLoading = false) }
                }
        }
    }

    fun changeSortType(newSortType: SortType) {
        _uiState.update { current ->
            val sortedPoints = when (newSortType) {
                SortType.DATE -> current.points.sortedByDescending { it.updatedAt }
                SortType.ALPHABETICAL -> current.points.sortedBy { it.title }
            }
            current.copy(points = sortedPoints, sortType = newSortType)
        }
    }
}