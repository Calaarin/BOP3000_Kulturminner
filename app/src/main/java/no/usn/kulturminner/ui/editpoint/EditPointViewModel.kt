package no.usn.kulturminner.ui.editpoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.repository.PointRepository

class EditPointViewModel(
    private val pointRepository: PointRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPointUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // loadPoint("p1")          // id skal egentlig hentes fra punktlisten i Admin Dashboard, sendt via NavHost
    }

    fun loadPoint(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            pointRepository.getPoint(id)
                .onSuccess { point ->
                    _uiState.update {
                        it.copy(
                            pointId = point.id,
                            title = point.title,
                            lat = point.lat,
                            lng = point.lng,
                            radius = point.radius,
                            audioUrl = point.audioUrl ?: "",
                            sections = point.sections.map { it.toUiState() },
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updatePoint() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isSaving = true,
                isSuccess = false,          // Boolean-sjekker må tilbakestilles ved start av utføring
                error = null,
            ) }

            val pointToUpdate = Point(
                id = _uiState.value.pointId,
                title = _uiState.value.title,
                lat = _uiState.value.lat,
                lng = _uiState.value.lng,
                radius = _uiState.value.radius,
                audioUrl = _uiState.value.audioUrl.ifBlank { null },
                sections = _uiState.value.sections.map { it.toSection() }
            )

            pointRepository.updatePoint(pointToUpdate)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSuccess = true) }         // Dette blir et flagg for at man kan fullføre redigeringen og navigeres tilbake til Admin dashboard
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    // Oppdateringsfunksjoner (brukes fra skjermen - blir mye funksjonsreferanser her)
    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateLat(lat: Double) = _uiState.update { it.copy(lat = lat) }
    fun updateLng(lng: Double) = _uiState.update { it.copy(lng = lng) }
    fun updateRadius(radius: Int) = _uiState.update { it.copy(radius = radius) }
    fun updateAudioUrl(audioUrl: String) = _uiState.update { it.copy(audioUrl = audioUrl) }

    fun addSection() = _uiState.update { it.copy(sections = it.sections + SectionUiState()) }
    fun removeSection(index: Int) = _uiState.update {
        it.copy(sections = it.sections.toMutableList().apply { removeAt(index) })
    }
    fun updateSection(index: Int, section: SectionUiState) = _uiState.update {
        it.copy(sections = it.sections.toMutableList().apply { set(index, section) })
    }
}