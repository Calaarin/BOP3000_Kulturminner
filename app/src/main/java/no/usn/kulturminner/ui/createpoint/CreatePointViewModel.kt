package no.usn.kulturminner.ui.createpoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.repository.PointRepository

class CreatePointViewModel(
    private val pointRepository: PointRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePointUiState())
    val uiState = _uiState.asStateFlow()

    // Hovedfunksjonen for å opprette punkt
    fun createPoint() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isSaving = true,
                isSuccess = false,          // Boolean-sjekker må tilbakestilles ved start av utføring
                error = null,
            ) }

            val point = Point(
                // id settes automatisk fra server ved opprettelse
                title = _uiState.value.title,
                lat = _uiState.value.lat,
                lng = _uiState.value.lng,
                radius = _uiState.value.radius,
                audioUrl = _uiState.value.audioUrl.ifBlank { null },
                sections = _uiState.value.sections.map { it.toSection() }
            )

            pointRepository.createPoint(point)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    // ====== Funksjoner for å oppdatere skjema (brukes fra skjermen - blir mye funksjonsreferanser her) =====

    fun updateTitle(title: String) = _uiState.update {
        it.copy(title = title)
    }

    fun updateLat(lat: Double) = _uiState.update {
        it.copy(lat = lat)
    }

    fun updateLng(lng: Double) = _uiState.update {
        it.copy(lng = lng)
    }

    fun updateRadius(radius: Int) = _uiState.update {
        it.copy(radius = radius)
    }

    fun updateAudioUrl(audioUrl: String) = _uiState.update {
        it.copy(audioUrl = audioUrl)
    }

    fun updateSection(index: Int, section: SectionUiState) = _uiState.update {
        it.copy(sections = it.sections.toMutableList().apply { set(index, section) })
    }

    // ===== Oppdatering av antall seksjoner =====

    fun addSection() = _uiState.update {
        it.copy(sections = it.sections + SectionUiState())
    }

    fun removeSection(index: Int) = _uiState.update {
        it.copy(sections = it.sections.toMutableList().apply { removeAt(index) })
    }

    // Oppdatert funksjon
    fun setSectionCount(count: Int) {
        if (count < 1 || count > 5) return

        _uiState.update { current ->
            current.copy(
                selectedSectionCount = count,
                sections = List(count) { index ->
                    // Behold eksisterende data hvis vi har flere seksjoner enn før
                    current.sections.getOrNull(index) ?: SectionUiState()
                }
            )
        }
    }
}