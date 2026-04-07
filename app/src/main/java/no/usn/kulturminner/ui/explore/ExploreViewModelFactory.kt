package no.usn.kulturminner.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.usn.kulturminner.data.repository.PointRepository
import no.usn.kulturminner.data.repository.RouteRepository   // Denne er ikke ferdig så jeg kommenterer ut fila foreløpig
/*

class ExploreViewModelFactory(
    private val pointRepository: PointRepository,
    private val routeRepository: RouteRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            return ExploreViewModel(pointRepository, routeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
 */