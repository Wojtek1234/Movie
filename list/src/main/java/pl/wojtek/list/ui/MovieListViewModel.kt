package pl.wojtek.list.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.base.BaseViewModel
import pl.wojtek.list.domain.LoadMoviesUseCase
import pl.wojtek.list.domain.Movie

/**
 *
 */


internal class MovieListViewModel(val useCase: LoadMoviesUseCase, coroutineUtils: CoroutineUtils) : BaseViewModel(coroutineUtils) {
    private val moviesProcessor = MutableLiveData<List<Movie>>()
    val moviesStream: LiveData<List<Movie>> get() = moviesProcessor
    fun loadMore() {
        TODO("Not yet implemented")
    }

}