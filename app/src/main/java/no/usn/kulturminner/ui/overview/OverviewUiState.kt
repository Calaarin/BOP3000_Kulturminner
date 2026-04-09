package no.usn.kulturminner.ui.overview

import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.model.User

data class OverviewUiState(
    val isUserLoading: Boolean = false,
    val isPointListLoading: Boolean = false,

    val userError: String? = null,
    val pointError: String? = null,

    val user: User? = null,
    val points: List<Point> = emptyList(),

    val sortType: SortType = SortType.DATE,

    val isSortedAlphabetically: Boolean = false
)

enum class SortType {
    DATE,           // Sist oppdatert øverst
    ALPHABETICAL    // Alfabetisk fra A-Å
}