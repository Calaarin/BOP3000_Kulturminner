package no.usn.kulturminner.ui.explore

import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.model.Route
import kotlin.Double

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
    val activePoint: Point? = null, // Skal brukes til serverdata
    val pointNearby: Point? = null, // Til foreløpig dummydata
    val userLat: Double = 0.0,
    val userLng: Double = 0.0,

    // Simulering (gammel versjon - startposisjoer)
    // val simulatedLat: Double = 59.4120,  // startposisjon
    // val simulatedLng: Double = 9.0590,

    // Ny versjon
    val isUsingSimulation: Boolean = false,
    val simulatedLat: Double = 59.41694,  // startposisjon basert på Bø Prestegård
    val simulatedLng: Double = 9.05833,
    val isSimulationPaused: Boolean = false,
    val simulationSpeedMultiplier: Float = 1f  // Kan økes/reduseres i trinn mellom 0.25, 0.33, 0.5, 1, 2, 3, 4
)