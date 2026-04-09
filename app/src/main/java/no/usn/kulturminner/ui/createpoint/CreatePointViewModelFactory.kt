package no.usn.kulturminner.ui.createpoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.usn.kulturminner.data.repository.PointRepository

class CreatePointViewModelFactory(
    private val pointRepository: PointRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePointViewModel::class.java)) {
            return CreatePointViewModel(pointRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}