package pl.wojtek.testutils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.junit.Assert

/**
 *
 */


fun <T> LiveData<T>.test(): TestLiveDataObserver<T> {
    return TestLiveDataObserver(this)
}

class TestLiveDataObserver<T>(
    private val liveData: LiveData<T>
) {
    private val values = mutableListOf<T>()

    private val observer: Observer<T>

    init {
        observer = Observer {
            values.add(it)
        }
        liveData.observeForever(observer)
    }

    fun assertNoValues(): TestLiveDataObserver<T> {
        Assert.assertEquals(emptyList<T>(), this.values)
        return this
    }

    fun assertValues(vararg values: T): TestLiveDataObserver<T> {
        Assert.assertEquals(values.toList(), this.values)
        return this
    }

    fun finish() {
        liveData.removeObserver(observer)
    }
}