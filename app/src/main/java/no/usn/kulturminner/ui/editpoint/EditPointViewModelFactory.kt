package no.usn.kulturminner.ui.editpoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.usn.kulturminner.data.repository.PointRepository

class EditPointViewModelFactory(
    private val pointRepository: PointRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditPointViewModel::class.java)) {
            return EditPointViewModel(pointRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}