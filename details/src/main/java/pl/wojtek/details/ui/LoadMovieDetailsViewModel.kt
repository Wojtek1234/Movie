package pl.wojtek.details.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.base.BaseViewModel
import pl.wojtek.details.domain.LoadMovieDetailsUseCase
import pl.wojtek.details.domain.MovieDetails

/**
 *
 */


internal class LoadMovieDetailsViewModel(
    private val loadMovieDetailsUseCase: LoadMovieDetailsUseCase,
    coroutineUtils: CoroutineUtils
) : BaseViewModel(coroutineUtils) {

    private val movieDetailsProcessor = MutableLiveData<MovieDetails>()
    val movieDetailsStream: LiveData<MovieDetails> get() = movieDetailsProcessor

    private val favouriteProcessor = MutableLiveData<Boolean>()
    val favouriteStream: LiveData<Boolean> get() = favouriteProcessor

    init {
        reload()
    }

    fun reload() {
        launchWithProgress {
            withContext(coroutineUtils.io) {
                loadMovieDetailsUseCase.loadMovieDetails()
            }.apply {
                movieDetailsProcessor.postValue(this)
                favouriteProcessor.postValue(this.isFavourite)
            }
        }
    }

    fun changeFavourite() {
        viewModelScope.launch(errorHandler) {
            favouriteProcessor.postValue(loadMovieDetailsUseCase.changeFavourites())
        }
    }
}