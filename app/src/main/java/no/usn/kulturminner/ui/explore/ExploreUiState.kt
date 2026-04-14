package no.usn.kulturminner.ui.explore

import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.model.Route

data class ExploreUiState (
    // Lastingtilstand
    val areRoutesLoading: Boolean = false,
    val isPointListLoading: Boolean = false,
    val isPointLoading: Boolean = false,

    // Error
    val routeError: String? = null,
    val pointListError: String? = null,
    val pointError: String? = null,

    // Data
    val routes: List<Route> = emptyList(),
    val points: List<Point> = emptyList(),
    val activePoint: Point? = null,
    val pointNearby: Point? = null,
    val userLat: Double = 0.0,
    val userLng: Double = 0.0
)