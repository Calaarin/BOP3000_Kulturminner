package no.usn.kulturminner.ui.explore

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.repository.LocationRepository
import no.usn.kulturminner.data.repository.PointRepository
import no.usn.kulturminner.data.repository.RouteRepository
import no.usn.kulturminner.data.model.LatLng

class ExploreViewModel(
    private val pointRepository: PointRepository,
    private val locationRepository: LocationRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var simulationJob: Job? = null
    private var locationJob: Job? = null

    init {
        fetchAllPoints()
        fetchRoutes()
        locationJob = viewModelScope.launch { runLocationUpdates() }
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

    fun toggleLocationMode() {
        val usingSimulation = !_uiState.value.isUsingSimulation
        _uiState.update { it.copy(isUsingSimulation = usingSimulation) }

        if (usingSimulation) {
            locationJob?.cancel()
            simulationJob = viewModelScope.launch { runRouteBasedSimulation() }
        } else {
            simulationJob?.cancel()
            locationJob = viewModelScope.launch { runLocationUpdates() }
        }
    }

    // Starte posisjonsopdateringer til kart
    private suspend fun runLocationUpdates() {
        locationRepository.locationFlow().collect { location ->
            _uiState.update { it.copy(
                userLat = location.latitude,
                userLng = location.longitude
            )}
            checkProximityToPoints(location)
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

    // Simulert brukerposisjon – starter utenfor radius, beveger seg inn og ut - GAMMEL VERSJON
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

    private suspend fun runRouteBasedSimulation() {
        // Vent til ruter er lastet
        while (_uiState.value.routes.isEmpty()) {
            delay(200L)
        }

        val routes = _uiState.value.routes

        // Sett sammen ruter i ønsket rekkefølge basert på index
        // Rekkefølgen tilpasses etter at du ser hvilke fid-er som matcher ønsket sti
        // Start med å bruke alle ruter i rekkefølge og loop

        // Eksempel: velg ruter i spesifikk rekkefølge basert på index i lista
        val orderedCoordinates = listOf(0, 1, 2, 3, 4)  // justert etter testing av indeksering av ruta
            .mapNotNull { routes.getOrNull(it) }
            .flatMap { it.coordinates }

        // Indekser i rutedata (deler av ruta):
        // 2: fra Kirka. 3: fra Gullbring. 0: fra prestegård. 5: USN. 4: ? 1: ?

        val smoothPath = buildSmoothPath(orderedCoordinates, stepsPerSegment = 15)

        while (true) {  // loop
            smoothPath.forEach { (lat, lng) ->
                // Vent mens pauset
                while (_uiState.value.isSimulationPaused) {
                    delay(100L)
                }

                _uiState.update { it.copy(
                    simulatedLat = lat,
                    simulatedLng = lng
                )}

                val location = Location("simulert").apply {
                    latitude = lat
                    longitude = lng
                }
                checkProximityToPoints(location)

                delay((50L / uiState.value.simulationSpeedMultiplier).toLong())
            }
        }
    }

    // Oppdatering av tilstand på pausing av simulert brukerbevegelse
    fun toggleSimulationPause() {
        _uiState.update { it.copy(isSimulationPaused = !it.isSimulationPaused) }
    }

    // Hjelpefunksjon for interpolering i simulert bevegelse
    private fun interpolate(
        start: no.usn.kulturminner.data.model.LatLng,
        end: no.usn.kulturminner.data.model.LatLng,
        steps: Int
    ): List<Pair<Double, Double>> {
        return (0..steps).map { i ->
            val fraction = i / steps.toDouble()
            Pair(
                start.lat + (end.lat - start.lat) * fraction,
                start.lng + (end.lng - start.lng) * fraction
            )
        }
    }

    private fun buildSmoothPath(coordinates: List<LatLng>, stepsPerSegment: Int): List<Pair<Double, Double>> {
        if (coordinates.size < 2) return emptyList()
        return (0 until coordinates.lastIndex).flatMap { i ->
            interpolate(coordinates[i], coordinates[i + 1], stepsPerSegment)
        }
    }

    // Økning av fart i simulert bevegelse
    fun increaseSpeed() {
        val current = _uiState.value.simulationSpeedMultiplier
        val next = when (current) {
            0.25f -> 0.33f
            0.33f -> 0.5f
            0.5f -> 1f
            1f -> 2f
            2f -> 3f
            3f -> 4f
            else -> current  // allerede på maks
        }
        _uiState.update { it.copy(simulationSpeedMultiplier = next) }
    }

    // Redusering av fart i simulert bevegelse
    fun decreaseSpeed() {
        val current = _uiState.value.simulationSpeedMultiplier
        val next = when (current) {
            4f -> 3f
            3f -> 2f
            2f -> 1f
            1f -> 0.5f
            0.5f -> 0.33f
            0.33f -> 0.25f
            else -> current  // allerede på min
        }
        _uiState.update { it.copy(simulationSpeedMultiplier = next) }
    }

    // ======================================= MEDIAPANEL =======================================

    // Hente et enkelt punkt til MediaPanel (dummydata fra PointRepository)
    fun fetchSinglePoint(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPointLoading = true, pointError = null) }

            pointRepository.getSingleDummyPoint(id)     // Bruk getPoint(id) for serverdata (trengs ikke), og getSingleDummyPoint(id) for lokale dummydata
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