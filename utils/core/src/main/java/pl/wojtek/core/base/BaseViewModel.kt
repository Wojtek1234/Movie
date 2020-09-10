package pl.wojtek.core.base

import androidx.lifecycle.*
import kotlinx.coroutines.*
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.common.ConsumableValue
import pl.wojtek.core.errors.ErrorWrapper

/**
 *
 */


abstract class BaseViewModel(protected val coroutineUtils: CoroutineUtils) : ViewModel() {
    protected val errorProcessor: MutableLiveData<ConsumableValue<ErrorWrapper>> = MutableLiveData()

    val errorWrapperStream: LiveData<ConsumableValue<ErrorWrapper>> get() = errorProcessor

    protected open val errorHandler = CoroutineExceptionHandler { _, throwable ->
        errorProcessor.postValue(ConsumableValue(coroutineUtils.produce(throwable)))

        showProgressProcessor.postValue(false)
    }

    protected val showProgressProcessor: MutableLiveData<Boolean> = MutableLiveData(false)

    val showProgressStream: LiveData<Boolean>
        get() = showProgressProcessor.distinctUntilChanged()

    fun launchWithProgress(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(errorHandler) {
            showProgressProcessor.postValue(true)
            block()
            showProgressProcessor.postValue(false)
        }
    }
}


fun BaseViewModel.repeatRequest(timeOfDelay: Long = 1000, scope: CoroutineScope = viewModelScope, block: () -> Unit): Job {
    return scope.launch {
        while (isActive) {
            block()
            delay(timeOfDelay)
        }
    }
}


