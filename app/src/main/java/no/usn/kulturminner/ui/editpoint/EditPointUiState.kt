package no.usn.kulturminner.ui.editpoint

import android.net.Uri
import no.usn.kulturminner.data.model.Section

data class EditPointUiState(
    // Lasting / lagring / error tilstander / tilbakemeldinger til bruker
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,         // Brukes som sjekk før man navigerer tilbake etter lagring
    val popupMessage: String? = null,       // Brukes til å gi feilmeldinger til bruker i popup-vindu
    val savingMessage: String = "Lagrer endringer...",

    // Seksjonshåndtering
    var sectionsExpanded: Boolean = false,
    val selectedSectionCount: Int = 1,
    val isSectionCountDropdownExpanded: Boolean = false,

    // Tilstand på om audiofil er lastet opp
    val isAudioUploaded: Boolean = false,   // Denne styrer om tekstfelt for lydfil er låst

    // Original-URLer tas vare på for sammenligning på om bruker har endret eller fjernet de fra tekstfelt
    val originalAudioUrl: String = "",
    val originalSectionUrls: List<OriginalSectionUrls> = emptyList(),

    // Punkt-data (dataene oppdateres både fra server og brukerinput)
    val pointId: String? = null,
    val title: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val radius: String = "",                // Endrer denne til String for bedre UX
    val audioUrl: String = "",
    val audioUri: Uri? = null,
    val sections: List<SectionUiState> = List(1) { SectionUiState() }
)

// State for hver seksjon i seksjonslista (del av punkt-data)
data class SectionUiState(
    val id: String? = null,
    val heading: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val imageUri: Uri? = null,                  // Lokal mediafil valgt via PhotoPicker
    val videoUrl: String = "",
    val videoUri: Uri? = null,

    // Tilstand på om mediefiler er lastet opp innen seksjon
    val isImageUploaded: Boolean = false,       // Styrer om tekstfelt er låst
    val isVideoUploaded: Boolean = false,
)



// ===== Mapperfunksjoner mellom Domain/dataklasse og UiState (Extension-funksjoner for UiState) =====

// Denne brukes i funksjonen loadPoint i EditPointViewModel
fun Section.toUiState() = SectionUiState(
    id = id,
    heading = heading ?: "",
    text = text ?: "",
    imageUrl = imageUrl ?: "",
    videoUrl = videoUrl ?: ""
)

// Denne brukes i funksjonen updatePoint  i EditPointViewModel
fun SectionUiState.toSection() = Section(
    id = id,
    heading = heading.ifBlank { null },
    text = text.ifBlank { null },
    imageUrl = imageUrl.ifBlank { null },
    videoUrl = videoUrl.ifBlank { null }
)

data class OriginalSectionUrls(
    val imageUrl: String = "",
    val videoUrl: String = ""
)