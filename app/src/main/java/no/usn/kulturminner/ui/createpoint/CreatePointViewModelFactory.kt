package no.usn.kulturminner.ui.createpoint

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.usn.kulturminner.data.local.TokenStorage
import no.usn.kulturminner.data.repository.PointRepository

class CreatePointViewModelFactory(
    private val pointRepository: PointRepository,
    private val tokenStorage: TokenStorage,
    private val appContext: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePointViewModel::class.java)) {
            return CreatePointViewModel(pointRepository, tokenStorage, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}