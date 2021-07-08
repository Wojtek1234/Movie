package pl.wojtek.list.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.base.BaseViewModel
import pl.wojtek.list.domain.filter.FilterMoviesUseCase

/**
 *
 */


internal class ProvideHintOptionsViewModel(
    private val filterMoviesUseCase: FilterMoviesUseCase,
    coroutineUtils: CoroutineUtils) :
    BaseViewModel(coroutineUtils) {

    private val queryHintsProcessor = MutableLiveData<List<String>>()
    val queryHintsStream: LiveData<List<String>> get() = queryHintsProcessor.distinctUntilChanged()

    private var lastLoad: Job? = null

    fun setQuery(query: String) {
        lastLoad?.cancel()
        viewModelScope.launch(errorHandler) {
            withContext(coroutineUtils.io) {
                filterMoviesUseCase.provideFilterHints(query)
            }.apply {
                queryHintsProcessor.postValue(map { it.text })
            }
        }
    }
}