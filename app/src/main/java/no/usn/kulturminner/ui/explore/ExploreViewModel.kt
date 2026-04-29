package no.usn.kulturminner.ui.explore

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.repository.LocationRepository
import no.usn.kulturminner.data.repository.PointRepository
import no.usn.kulturminner.data.repository.RouteRepository

class ExploreViewModel(
    private val pointRepository: PointRepository,
    private val locationRepository: LocationRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAllPoints()
        // fetchRoutes() // Legger til når Arne har retta formatet
        // fetchSinglePoint("p2") // Bytt til enten "p1", "p2", "p3", "p4" eller p5 for å teste layout i MediaPanel av andre datasammensetninger
        // startLocationUpdates()
        startSimulatedMovement()
    }

    // ======================================= EXPLOREMAP =======================================

    // Hente ruter til kart (fra oppdragsgivers server - statisk fil-hosting)
    fun fetchRoutes() {
        viewModelScope.launch {
            _uiState.update { it.copy(areRoutesLoading = true, routeError = null) }

            routeRepository.getRoutes()
                .onSuccess { routes ->
                    _uiState.update { it.copy(routes = routes) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(routeError = e.message, areRoutesLoading = false) }
                }
        }
    }

    // Hente alle punkter til kart
    fun fetchAllPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPointListLoading = true, pointError = null) }

            pointRepository.getAllPoints()           // Bruk getAllPoints() til serverdata og getDummyPoints() til lokale dummydata
                .onSuccess { points ->
                    _uiState.update { it.copy(points = points, isPointListLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(pointError = e.message, isPointListLoading = false) }
                }
        }
    }

    // Starte posisjonsopdateringer til kart
    private fun startLocationUpdates() {
        viewModelScope.launch {
            locationRepository.locationFlow().collect { location ->
                _uiState.update { it.copy(
                    userLat = location.latitude,
                    userLng = location.longitude
                )}
                // checkProximityToPoints(location)
            }
        }
    }

    private fun checkProximityToPoints(location: Location) {
        val points = _uiState.value.points
        val pointNearby = points.firstOrNull { point ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                location.latitude, location.longitude,
                point.lat, point.lng,
                distance
            )
            distance[0] <= point.radius
        }
        _uiState.update { it.copy(activePoint = pointNearby) }
    }

    // Simulert brukerposisjon – starter utenfor radius, beveger seg inn og ut
    fun startSimulatedMovement() {
        val targetLat = 59.41044  // Gullbring Kulturanlegg
        val targetLng = 9.06212

        // Rute: start langt unna, gå inn i radius, stå stille, og gå ut igjen
        val waypoints = listOf(
            // Står stille langt unna
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),
            Pair(59.4120, 9.0590),

            // Starter å bevege seg
            Pair(59.4119, 9.0593),
            Pair(59.4118, 9.0596),
            Pair(59.4117, 9.0598),

            // Beveger seg nærmere
            Pair(59.4115, 9.0600),
            Pair(59.4114, 9.0603),
            Pair(59.4113, 9.0606),
            Pair(59.4112, 9.0608),

            Pair(59.4110, 9.0610),
            Pair(59.4109, 9.0612),
            Pair(59.41085, 9.06135),
            Pair(59.4108, 9.0615),

            Pair(59.41075, 9.0616),
            Pair(59.4107, 9.0617),
            Pair(59.41065, 9.0618),
            Pair(59.4106, 9.0619),

            // Nær radius-grense
            Pair(59.41055, 9.0620),
            Pair(59.4105, 9.06205),
            Pair(59.41045, 9.06208),
            Pair(59.41042, 9.06210),

            // Innenfor radius
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),
            Pair(59.41040, 9.06212),


            // Ut igjen
            Pair(59.41045, 9.06220),
            Pair(59.4105, 9.0623),
            Pair(59.4106, 9.0625),
            Pair(59.4107, 9.0628),

            Pair(59.4110, 9.0635),
            Pair(59.4112, 9.0640),
            Pair(59.4114, 9.0645),
            Pair(59.4115, 9.0650),

            // Langt unna igjen
            Pair(59.4117, 9.0658),
            Pair(59.4118, 9.0662),
            Pair(59.4119, 9.0666),
            Pair(59.4120, 9.0670)
        )

        viewModelScope.launch {
            waypoints.forEach { (lat, lng) ->
                _uiState.update { it.copy(
                    simulatedLat = lat,
                    simulatedLng = lng
                )}

                // Bruk checkProximityToPoints med simulert posisjon
                val simulertLocation = Location("simulert").apply {
                    latitude = lat
                    longitude = lng
                }
                checkProximityToPoints(simulertLocation)

                delay(500L)
            }
        }
    }

    // ======================================= MEDIAPANEL =======================================

    // Hente et enkelt punkt til MediaPanel
    fun fetchSinglePoint(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPointLoading = true, pointError = null) }

            pointRepository.getSingleDummyPoint(id)     // Bruk getPoint(id) for serverdata og getSingleDummyPoint(id) for lokale dummydata
                .onSuccess { point ->
                    _uiState.update {
                        it.copy(
                            activePoint = point,
                            isPointLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isPointLoading = false,
                            pointError = error.message
                        )
                    }
                }
        }
    }
}