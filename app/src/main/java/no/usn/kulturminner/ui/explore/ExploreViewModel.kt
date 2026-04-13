package no.usn.kulturminner.ui.explore

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.repository.LocationRepository
import no.usn.kulturminner.data.repository.PointRepository

class ExploreViewModel(
    private val pointRepository: PointRepository,
    private val locationRepository: LocationRepository
    // routeRepository legges til senere
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchAllPoints()
        fetchSinglePoint("p3") // Bytt til enten "p2" "p3" eller "p4" for å teste layout av andre datasammensetninger
    }

    // ======================================= EXPLOREMAP =======================================

    // Hente alle punkt til kart
    fun fetchAllPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPointListLoading = true, pointError = null) }

            pointRepository.getDummyPoints()           // Bytt til getAllPoints() senere når vi skal hente data fra server
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
                sjekkNærhetTilPunkter(location)
            }
        }
    }

    private fun sjekkNærhetTilPunkter(location: Location) {
        val points = _uiState.value.points
        val pointNearby = points.firstOrNull { punkt ->
            val avstand = FloatArray(1)
            Location.distanceBetween(
                location.latitude, location.longitude,
                punkt.lat, punkt.lng,
                avstand
            )
            avstand[0] <= punkt.radius
        }
        _uiState.update { it.copy(activePoint = pointNearby) }
    }

    // ======================================= MEDIAPANEL =======================================

    // Hente et enkelt punkt til MediaPanel
    fun fetchSinglePoint(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPointLoading = true, pointError = null) }

            pointRepository.getSingleDummyPoint(id)     // Bytt til getPoint(id) senere når vi skal hente data fra server
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