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

    // Midlertidig hardkodet brukerID basert på det som ligger i databasen
    val dummyId: String = "u1" // id-en til dummybruker (lokale data)
    val arneId: String = "0667a905-b6e3-42a8-9020-dcc387d24f1a"
    val toreId: String = "c9329389-90ac-472f-8497-8bce166b3290"
    val userId: String =  arneId // byttes etter behov (må byttes likt i 3 ViewModels: Overview, CreatePoint og EditPoint)

    // Hovedfunksjonen for å opprette punkt
    fun createPoint() {
        viewModelScope.launch {
            // Nullstilling av feil/suksess/statuser
            _uiState.update { it.copy(
                isSaving = true,
                isSuccess = false,
                popupMessage = null
            ) }

            // Validering av radius
            val radiusInt = _uiState.value.radius.toIntOrNull()
            if (radiusInt == null || radiusInt < 5) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        popupMessage = "Radius må være et heltall på minst 5 meter"    // Vi kan diskutere hva som burde være minimum
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

            val point = Point(
                userId = userId,
                title = _uiState.value.title,
                lat = _uiState.value.lat,
                lng = _uiState.value.lng,
                radius = radiusInt,
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