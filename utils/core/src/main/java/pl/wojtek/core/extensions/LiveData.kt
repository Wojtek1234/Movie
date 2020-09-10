package pl.wojtek.core.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.*

inline fun <reified T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T) -> Unit) = observe(owner, Observer { observer(it) })

inline fun <reified T> Fragment.observe(liveData: LiveData<T>, crossinline observer: (T) -> Unit) = liveData.observe(viewLifecycleOwner, Observer { observer(it) })

fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block.invoke(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block.invoke(this.value, liveData.value)
    }
    return result
}
