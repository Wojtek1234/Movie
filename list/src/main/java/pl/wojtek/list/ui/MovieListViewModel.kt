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
import pl.wojtek.list.domain.LoadMoviesUseCase
import pl.wojtek.list.domain.Movie

/**
 *
 */


internal class MovieListViewModel(
    private val useCase: LoadMoviesUseCase,
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

    override fun onCleared() {
        super.onCleared()
        useCase.clear()
    }
}