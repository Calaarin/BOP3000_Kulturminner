package no.usn.kulturminner.ui.explore

import no.usn.kulturminner.data.model.Point
import okhttp3.Route

data class ExploreUiState (
    val areRoutesLoading: Boolean = false,
    val isPointListLoading: Boolean = false,
    val isPointLoading: Boolean = false,

    val routeError: String? = null,
    val pointListError: String? = null,
    val pointError: String? = null,

    val routes: List<Route> = emptyList(),
    val points: List<Point> = emptyList(),
    val activePoint: Point? = null,   // Brukes for testing av MediaPanel når et punkt er innenfor radius
    val userLat: Double = 0.0,
    val userLng: Double = 0.0
)