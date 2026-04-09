package no.usn.kulturminner.ui.createpoint

import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.model.Section
import no.usn.kulturminner.ui.editpoint.EditPointUiState

data class CreatePointUiState (
    // Lasting / lagring / error tilstander
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,         // Brukes som sjekk før man navigerer tilbake etter lagring
    val popupMessage: String? = null,        // Brukes til å gi feilmelding om ugyldig verdi for radius

    // Punkt-data (dataene oppdateres både fra server og brukerinput)
    val pointId: String? = null,
    val title: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val radius: String = "",                 // Endrer denne til String for bedre UX
    val audioUrl: String = "",
    val sections: List<SectionUiState> = List(1) { SectionUiState() },

    // Seksjonshåndtering
    var sectionsExpanded: Boolean = false,
    val selectedSectionCount: Int = 1,
    val isSectionCountDropdownExpanded: Boolean = false,
)

// State for hver seksjon i seksjonslista (del av punkt-data)
data class SectionUiState(
    val id: String? = null,
    val heading: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val videoUrl: String = ""
)



// ===== Mapperfunksjon mellom Domain/dataklasse og UiState (Extension-funksjon for UiState) =====


// Brukes i funksjonen updatePoint i EditPointViewModel
fun SectionUiState.toSection() = Section(
    id = id,
    heading = heading.ifBlank { null },
    text = text.ifBlank { null },
    imageUrl = imageUrl.ifBlank { null },
    videoUrl = videoUrl.ifBlank { null }
)