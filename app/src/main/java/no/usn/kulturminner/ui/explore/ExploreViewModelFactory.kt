package no.usn.kulturminner.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.usn.kulturminner.data.repository.LocationRepository
import no.usn.kulturminner.data.repository.PointRepository

// Skal legge til "private val routeRepository: RouteRepository" senere når datakilde for ruter er på plass

class ExploreViewModelFactory(
    private val pointRepository: PointRepository,
    private val locationRepository: LocationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            return ExploreViewModel(pointRepository, locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}