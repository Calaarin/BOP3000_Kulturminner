package no.usn.kulturminner.ui.editpoint

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.api.DeleteMediaRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.repository.PointRepository
import no.usn.kulturminner.data.api.MediaApi
import no.usn.kulturminner.data.model.Section

class EditPointViewModel(
    private val pointRepository: PointRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPointUiState())
    val uiState = _uiState.asStateFlow()

    // id-en til dummybruker (lokale data)
    val dummyId: String = "u1"

    // Midlertidig hardkodet brukerID basert på det som ligger i databasen
    val arneId: String = "0667a905-b6e3-42a8-9020-dcc387d24f1a"
    val toreId: String = "c9329389-90ac-472f-8497-8bce166b3290"

    // Valgt brukerId blant de 3 over:
    val userId: String =  arneId // byttes etter behov (må byttes likt i 3 ViewModels: Overview, CreatePoint og EditPoint)

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
                            isLoading = false,

                            // Lagre originale URL-er for sammenligning ved lagring
                            originalAudioUrl = point.audioUrl ?: "",
                            originalSectionUrls = point.sections.map { section ->
                                OriginalSectionUrls(
                                    imageUrl = section.imageUrl ?: "",
                                    videoUrl = section.videoUrl ?: ""
                                )
                            }
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

            // Last opp mediafiler og bytt URI med URL
            val uploadState = _uiState.value

            // Lyd – enten opplastet fil eller innskrevet URL
            val finalAudioUrl = when {
                uploadState.isAudioUploaded && uploadState.audioUri != null -> {
                    // Tilbakemelding til bruker om at lydfil lastes opp til database (siden dette kan ta litt tid)
                    _uiState.update { it.copy(savingMessage = "Laster opp lydfil...") }
                    runCatching { uploadMedia(uploadState.audioUri, "audio") }
                        .getOrElse {
                            _uiState.update { it.copy(isSaving = false, popupMessage = "Feil ved opplasting av lydfil") }
                            return@launch
                        }
                }
                else -> uploadState.audioUrl.ifBlank { null }
            }

            // Seksjoner – last opp bilde og video per seksjon
            val finalSections = uploadState.sections.mapIndexed { index, seksjon ->
                // Bilde
                val finalImageUrl = when {
                    seksjon.isImageUploaded && seksjon.imageUri != null -> {
                        // Tilbakemelding til bruker om at bildefil lastes opp til database
                        _uiState.update { it.copy(savingMessage = "Laster opp bilde i seksjon ${index + 1}...") }
                        runCatching { uploadMedia(seksjon.imageUri, "image") }
                            .getOrElse {
                                _uiState.update { it.copy(isSaving = false, popupMessage = "Feil ved opplasting av bilde i seksjon ${index + 1}") }
                                return@launch
                            }
                    }
                    else -> seksjon.imageUrl.ifBlank { null }
                }
                // Video
                val finalVideoUrl = when {
                    seksjon.isVideoUploaded && seksjon.videoUri != null -> {
                        // Tilbakemelding til bruker om at videofil lastes opp til database
                        _uiState.update { it.copy(savingMessage = "Laster opp video i seksjon ${index + 1}...") }
                        runCatching { uploadMedia(seksjon.videoUri, "video") }
                            .getOrElse {
                                _uiState.update { it.copy(isSaving = false, popupMessage = "Feil ved opplasting av video i seksjon ${index + 1}") }
                                return@launch
                            }
                    }
                    else -> seksjon.videoUrl.ifBlank { null }
                }

                Section(
                    heading = seksjon.heading.ifBlank { null },
                    text = seksjon.text.ifBlank { null },
                    imageUrl = finalImageUrl,
                    videoUrl = finalVideoUrl
                )
            }

            // Slett erstattede eller fjernede Supabase-filer
            val deleteState = _uiState.value

            // Lyd
            if (isSupabaseUrl(deleteState.originalAudioUrl)) {
                val audioIsRemoved = finalAudioUrl == null
                val audioIsSwapped = finalAudioUrl != null && finalAudioUrl != deleteState.originalAudioUrl
                if (audioIsRemoved || audioIsSwapped) {
                    runCatching { MediaApi.service.deleteMedia(DeleteMediaRequest(deleteState.originalAudioUrl)) }
                    // Ignorerer feil ved sletting (ikke egentlig kritisk)
                }
            }

            // Bilde og video per seksjon
            deleteState.originalSectionUrls.forEachIndexed { index, original ->
                val final = finalSections.getOrNull(index)

                if (isSupabaseUrl(original.imageUrl)) {
                    val removed = final?.imageUrl == null
                    val swapped = final?.imageUrl != null && final.imageUrl != original.imageUrl
                    if (removed || swapped) {
                        runCatching { MediaApi.service.deleteMedia(DeleteMediaRequest(original.imageUrl)) }
                    }
                }

                if (isSupabaseUrl(original.videoUrl)) {
                    val removed = final?.videoUrl == null
                    val swapped = final?.videoUrl != null && final.videoUrl != original.videoUrl
                    if (removed || swapped) {
                        runCatching { MediaApi.service.deleteMedia(DeleteMediaRequest(original.videoUrl)) }
                    }
                }
            }

            // Bytter tilbakemelding til bare lagring av selve punktet
            _uiState.update { it.copy(savingMessage = "Lagrer endringer...") }

            val pointToUpdate = Point(
                id = _uiState.value.pointId,
                userId = userId, // midlertidig
                title = _uiState.value.title,
                lat = _uiState.value.lat,
                lng = _uiState.value.lng,
                radius = radiusInt,
                audioUrl = finalAudioUrl,
                sections = finalSections
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

    // ====== Hjelpefunksjoner for konvertering av Uri til MultiPartBody og Mediaopplasting ======

    private fun Uri.toMultipartPart(name: String): MultipartBody.Part {
        val mimeType = context.contentResolver.getType(this) ?: "application/octet-stream"
        val inputStream = context.contentResolver.openInputStream(this)!!
        val bytes = inputStream.readBytes()
        inputStream.close()
        val requestBody = bytes.toRequestBody(mimeType.toMediaType())
        val fileName = getFileName(this)
        return MultipartBody.Part.createFormData(name, fileName, requestBody)
    }

    private suspend fun uploadMedia(uri: Uri, type: String): String {
        val part = uri.toMultipartPart("file")
        return MediaApi.service.uploadMedia(type, part).url
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

    // ======= Funksjoner for å legge til og fjerne mediefiler (opplasting fra Android enhet) =======

    fun selectImage(sectionIndex: Int, uri: Uri) {
        val extension = context.contentResolver
            .getType(uri)  // dette returnerer MIME-type f.eks "image/jpeg" som brukes til formatsjekk

        val supported = listOf("image/jpeg", "image/jpg", "image/png")
        if (extension !in supported) {
            _uiState.update { it.copy(popupMessage = "Filformat støttes ikke. Bruk jpg, jpeg eller png.") }
            return
        }

        _uiState.update { state ->
            val sections = state.sections.toMutableList()
            sections[sectionIndex] = sections[sectionIndex].copy(
                imageUri = uri,
                imageUrl = getFileName(uri),
                isImageUploaded = true
            )
            state.copy(sections = sections)
        }
    }

    fun selectVideo(sectionIndex: Int, uri: Uri) {
        val mimeType = context.contentResolver.getType(uri)
        val supported = listOf("video/mp4")

        if (mimeType !in supported) {
            _uiState.update { it.copy(popupMessage = "Filformat støttes ikke. Bruk mp4.") }
            return
        }

        _uiState.update { state ->
            val sections = state.sections.toMutableList()
            sections[sectionIndex] = sections[sectionIndex].copy(
                videoUri = uri,
                videoUrl = getFileName(uri),
                isVideoUploaded = true
            )
            state.copy(sections = sections)
        }
    }

    fun selectAudio(uri: Uri) {
        val mimeType = context.contentResolver.getType(uri)
        val supported = listOf("audio/mpeg", "audio/wav")

        if (mimeType !in supported) {
            _uiState.update { it.copy(popupMessage = "Filformat støttes ikke. Bruk mp3 eller wav.") }
            return
        }

        _uiState.update { it.copy(
            audioUri = uri,
            audioUrl = uri.lastPathSegment ?: "Audiofil valgt",
            isAudioUploaded = true
        )}
    }

    fun removeImage(sectionIndex: Int) {
        _uiState.update { state ->
            val sections = state.sections.toMutableList()
            sections[sectionIndex] = sections[sectionIndex].copy(
                imageUri = null,
                imageUrl = "",
                isImageUploaded = false
            )
            state.copy(sections = sections)
        }
    }

    fun removeVideo(sectionIndex: Int) {
        _uiState.update { state ->
            val sections = state.sections.toMutableList()
            sections[sectionIndex] = sections[sectionIndex].copy(
                videoUri = null,
                videoUrl = "",
                isVideoUploaded = false
            )
            state.copy(sections = sections)
        }
    }

    fun removeAudio() {
        _uiState.update { it.copy(
            audioUri = null,
            audioUrl = "",
            isAudioUploaded = false
        )}
    }

    // Hjelpefunsjon for å hente filnavn fra URI-stien (til visning i tekstfelt)
    fun getFileName(uri: Uri): String {
        var result = "Fil valgt"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex)
                }
            }
        }
        return result
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

    // ======= Funksjoner for å flytte seksjoner opp eller ned i seksjonslista =======

    // TODO: sett opp knapper med onClick i Screen

    fun moveSectionUp(index: Int) {
        if (index <= 0) return
        _uiState.update { state ->
            val sections = state.sections.toMutableList()
            val temp = sections[index]
            sections[index] = sections[index - 1]
            sections[index - 1] = temp
            state.copy(sections = sections)
        }
    }

    fun moveSectionDown(index: Int) {
        if (index >= _uiState.value.sections.size - 1) return
        _uiState.update { state ->
            val sections = state.sections.toMutableList()
            val temp = sections[index]
            sections[index] = sections[index + 1]
            sections[index + 1] = temp
            state.copy(sections = sections)
        }
    }

    // ===== Nullstilling av popupMessage =====
    fun dismissPopup() {
        _uiState.update { it.copy(popupMessage = null) }
    }

    // Hjelpefunksjon til sjekk om det er en Supabse-URL
    private fun isSupabaseUrl(url: String): Boolean =
        url.contains("supabase.co/storage")
}