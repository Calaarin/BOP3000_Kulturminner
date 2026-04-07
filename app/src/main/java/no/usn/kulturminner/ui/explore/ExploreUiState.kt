package no.usn.kulturminner.ui.explore

import no.usn.kulturminner.data.model.Point

data class ExploreUiState (
    val areRoutesLoading: Boolean = false,
    val arePointsLoading: Boolean = false,

    val routeError: String? = null,
    val pointError: String? = null,

    val points: List<Point> = emptyList(),
    val pointNearby: Point? = null   // Brukes for testing av MediaPanel når et punkt er innenfor radius
)