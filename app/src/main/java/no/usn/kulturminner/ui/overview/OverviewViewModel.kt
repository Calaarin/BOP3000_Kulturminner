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

    val dummyId: String = "u1" // id-en til dummybruker
    val perId: String = "71af648b-b071-4e3e-bb30-d318487d65de"
    val userId: String =  perId


    init {
        fetchUserData(userId)
        fetchMyPoints()
    }

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUserLoading = true, userError = null) }

            userRepository.getUser(userId)         // Bruk getUser(userId) for serverdata, getDummyUser(userId) for lokalt
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

            pointRepository.getMyPoints(userId)           // Bruk getMyPoints(userId) for server, getDummyPoints() for lokalt
                .onSuccess { points ->
                    _uiState.update { it.copy(points = points, isPointListLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(pointError = e.message, isPointListLoading = false) }
                }
        }
    }

    fun requestDeletePoint(id: String) {
        _uiState.update { it.copy(pointToDeleteId = id) }
    }

    fun confirmDeletePoint() {
        val id = _uiState.value.pointToDeleteId ?: return
        _uiState.update { it.copy(pointToDeleteId = null) }
        viewModelScope.launch {
            pointRepository.deletePoint(id)
                .onSuccess {
                    // Oppdaterer lista lokalt uten ny nettverksforespørsel
                    _uiState.update { state ->
                        state.copy(points = state.points.filter { it.id != id })
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(pointError = e.message) }
                }
        }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(pointToDeleteId = null) }
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