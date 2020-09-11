package pl.wojtek.list.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.base.BaseViewModel
import pl.wojtek.list.domain.Movie
import pl.wojtek.list.domain.favourite.ChangeFavouriteStatusUseCase
import pl.wojtek.list.domain.load.LoadMoviesUseCase

/**
 *
 */


internal class MovieListViewModel(
    private val useCase: LoadMoviesUseCase,
    private val changeFavouriteStatusUseCase: ChangeFavouriteStatusUseCase,
    coroutineUtils: CoroutineUtils
) : BaseViewModel(coroutineUtils) {

    private val moviesProcessor = MutableLiveData<List<Movie>>()
    val moviesStream: LiveData<List<Movie>> get() = moviesProcessor

    init {
        viewModelScope.launch {
            useCase.loading().collect {
                showProgressProcessor.postValue(it)
            }
        }

        viewModelScope.launch {
            useCase.loadedMovies().collect {
                moviesProcessor.postValue(it)
            }
        }

        viewModelScope.launch(errorHandler) {
            withContext(coroutineUtils.io) {
                delay(500)
                useCase.loadNextPage()
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch(errorHandler) {
            withContext(coroutineUtils.io) {
                useCase.loadNextPage()
            }
        }
    }

    fun setFilterQuery(query: String) {
        viewModelScope.launch {
            useCase.setFilterQuery(query)
        }
    }

    fun changeMovieFavouriteStatus(movie: Movie) {
        viewModelScope.launch(errorHandler) {
            changeFavouriteStatusUseCase.changeFavourite(movie.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        useCase.clear()
    }
}