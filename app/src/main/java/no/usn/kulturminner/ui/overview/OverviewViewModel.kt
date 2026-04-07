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

    // Holder på "default"-rekkefølge (simulert dato/tid) for sortering
    // private var originalPoints: List<String> = emptyList()

    init {
        fetchUserData(userId)
        fetchMyPoints()
    }
    /* Gammel dummydatafunksjon

    private fun fetchOverview() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            pointRepository.getDemoPoints()
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
     */

    // =================== Nye dummydatafunksjoner =======================

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUserLoading = true, userError = null) }

            userRepository.getUser(userId)
                .onSuccess { user ->
                    _uiState.update { it.copy(user = user, isUserLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(userError = e.message, isUserLoading = false) }
                }
        }
    }

    fun fetchMyPoints() {   // Må endres til fetchMyPoints(userId) hvis du vil begrense til innlogget bruker
        viewModelScope.launch {
            _uiState.update { it.copy(isPointListLoading = true, pointError = null) }

            pointRepository.getDummyPoints()           // Bruk getMyPoints() når serverkommunikasjon er klar
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

    /*

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

     */
}