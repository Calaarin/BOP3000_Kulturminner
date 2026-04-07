package no.usn.kulturminner.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.usn.kulturminner.data.repository.PointRepository
import no.usn.kulturminner.data.repository.UserRepository

class OverviewViewModelFactory(
    private val userRepository: UserRepository,
    private val pointRepository: PointRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(userRepository, pointRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}