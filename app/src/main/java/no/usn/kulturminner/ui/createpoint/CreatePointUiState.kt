package no.usn.kulturminner.ui.createpoint

import android.net.Uri
import no.usn.kulturminner.data.model.Section

data class CreatePointUiState (
    // Lasting / lagring / error tilstander / tilbakemeldinger til bruker
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,         // Brukes som sjekk før man navigerer tilbake etter lagring
    val popupMessage: String? = null,       // Brukes til å gi feilmeldinger til bruker i popup-vindu
    val savingMessage: String = "Oppretter punkt...",

    // Seksjonshåndtering
    var sectionsExpanded: Boolean = false,
    val selectedSectionCount: Int = 1,
    val isSectionCountDropdownExpanded: Boolean = false,

    // Tilstand på om audiofil er lastet opp
    val isAudioUploaded: Boolean = false,   // Denne styrer om tekstfelt for lydfil er låst

    // Punkt-data (dataene oppdateres både fra server og brukerinput)
    val pointId: String? = null,
    val title: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val radius: String = "",                 // Endrer denne til String for bedre UX
    val audioUrl: String = "",
    val audioUri: Uri? = null,
    val sections: List<SectionUiState> = List(1) { SectionUiState() }
)

// State for hver seksjon i seksjonslista (del av punkt-data)
data class SectionUiState(
    // Seksjonsdata
    val id: String? = null,
    val heading: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val imageUri: Uri? = null,                  // Lokal fil valgt via PhotoPicker
    val videoUrl: String = "",
    val videoUri: Uri? = null,

    // Tilstand på om mediefiler er lastet opp innen seksjon
    val isImageUploaded: Boolean = false,       // Styrer om tekstfelt er låst
    val isVideoUploaded: Boolean = false,
)



// ===== Mapperfunksjon mellom Domain/dataklasse og UiState (Extension-funksjon for UiState) =====


// Brukes i funksjonen updatePoint i EditPointViewModel (Kommer antageligvis ikke til å bli brukt lengre)
fun SectionUiState.toSection() = Section(
    id = id,
    heading = heading.ifBlank { null },
    text = text.ifBlank { null },
    imageUrl = imageUrl.ifBlank { null },
    videoUrl = videoUrl.ifBlank { null }
)