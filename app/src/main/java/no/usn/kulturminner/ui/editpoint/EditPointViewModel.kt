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

    // Midlertidig hardkodet brukerID basert på det som ligger i databasen
    val perId: String = "71af648b-b071-4e3e-bb30-d318487d65de"
    val userId: String =  perId // Utbyttbar midlertidig id til spørring mot databasen

    fun loadPoint(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            pointRepository.getPoint(id)           // Bruk getPoint(id) for serverdata og getSingleDummyPoint(id) for lokale data
                .onSuccess { point ->
                    _uiState.update {
                        it.copy(
                            pointId = point.id,
                            title = point.title,
                            lat = point.lat,
                            lng = point.lng,
                            radius = point.radius.toString(),
                            audioUrl = point.audioUrl ?: "",
                            sections = point.sections.map { it.toUiState() },
                            selectedSectionCount = point.sections.size,
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
                // Nullstilling av feil/suksess/statuser
                isSaving = true,
                isSuccess = false,
                popupMessage = null,
            ) }

            // Validering av radius
            val radiusInt = _uiState.value.radius.toIntOrNull()
            if (radiusInt == null || radiusInt < 5) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        popupMessage = "Radius må være et heltall på minst 5 meter"   // Vi kan diskutere hva som burde være minimum
                    )
                }
                return@launch
            }

            // Validering av innhold i seksjoner
            val tomSeksjon = _uiState.value.sections.indexOfFirst { seksjon ->
                seksjon.heading.isBlank() &&
                        seksjon.text.isBlank() &&
                        seksjon.imageUrl.isBlank() &&
                        seksjon.videoUrl.isBlank()
            }
            // Hvis en seksjon mangler innhold, gi bruker beskjed
            if (tomSeksjon != -1) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        popupMessage = "Seksjon ${tomSeksjon + 1} må ha minst ett innholdselement"
                    )
                }
                return@launch
            }

            // TODO: Bør kanskje også gjøres validering på rett format for URL-er
            //  (mulig at det er unødvendig egentlig, URL-er kan være feil uten å være feil format uansett)

            val pointToUpdate = Point(
                id = _uiState.value.pointId,
                userId = userId, // midlertidig
                title = _uiState.value.title,
                lat = _uiState.value.lat,
                lng = _uiState.value.lng,
                radius = radiusInt,
                audioUrl = _uiState.value.audioUrl.ifBlank { null },
                sections = _uiState.value.sections.map { it.toSection() }
            )

            pointRepository.updatePoint(pointToUpdate)
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

    fun updateRadius(radius: String) = _uiState.update {
        it.copy(radius = radius)
    }

    fun updateAudioUrl(audioUrl: String) = _uiState.update {
        it.copy(audioUrl = audioUrl)
    }

    fun updateSection(index: Int, section: SectionUiState) = _uiState.update {
        it.copy(sections = it.sections.toMutableList().apply { set(index, section) })
    }

    // ======= Håndtering av antall seksjon-skjema i skjermen =======

    fun expandSectionCountDropdown() {
        _uiState.update { it.copy(isSectionCountDropdownExpanded = true) }
    }

    fun dismissSectionCountDropdown() {
        _uiState.update { it.copy(isSectionCountDropdownExpanded = false) }
    }

    // Funksjon for dynamisk endring av antall seksjoner basert på dropdownliste med alternativ
    fun setSectionCount(count: Int) {
        if (count < 1 || count > 5) return
        _uiState.update { current ->
            current.copy(
                selectedSectionCount = count,
                isSectionCountDropdownExpanded = false,   // lukker dropdown automatisk
                sections = List(count) { index ->
                    current.sections.getOrNull(index) ?: SectionUiState()
                }
            )
        }
    }

    // ===== Nullstilling av popupMessage =====
    fun dismissPopup() {
        _uiState.update { it.copy(popupMessage = null) }
    }
}