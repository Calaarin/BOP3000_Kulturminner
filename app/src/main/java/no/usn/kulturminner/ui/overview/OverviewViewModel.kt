package no.usn.kulturminner.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.api.DeleteMediaRequest
import no.usn.kulturminner.data.api.MediaApi
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

    // id-en til dummybruker (lokale data)
    val dummyId: String = "u1"

    // Midlertidig hardkodet brukerID basert på det som ligger i databasen
    val arneId: String = "0667a905-b6e3-42a8-9020-dcc387d24f1a"
    val toreId: String = "c9329389-90ac-472f-8497-8bce166b3290"

    // Valgt brukerId blant de 3 over:
    val userId: String =  arneId // byttes etter behov (må byttes likt i 3 ViewModels: Overview, CreatePoint og EditPoint)

    // Kjøres ved navigering til Screen
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

        // Finn punktet i lista og samle Supabase-URL-er før vi sletter
        val point = _uiState.value.points.find { it.id == id }
        val mediaUrlsToDelete = buildList {
            point?.audioUrl?.let { if (it.contains("supabase.co/storage")) add(it) }
            point?.sections?.forEach { section ->
                section.imageUrl?.let { if (it.contains("supabase.co/storage")) add(it) }
                section.videoUrl?.let { if (it.contains("supabase.co/storage")) add(it) }
            }
        }

        _uiState.update { it.copy(pointToDeleteId = null) }

        viewModelScope.launch {
            pointRepository.deletePoint(id)
                .onSuccess {
                    // Oppdaterer lista lokalt uten ny nettverksforespørsel
                    _uiState.update { state ->
                        state.copy(points = state.points.filter { it.id != id })
                    }

                    // Sletter mediefiler i bakgrunnen – ignorerer bare feil
                    mediaUrlsToDelete.forEach { url ->
                        runCatching { MediaApi.service.deleteMedia(DeleteMediaRequest(url)) }
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