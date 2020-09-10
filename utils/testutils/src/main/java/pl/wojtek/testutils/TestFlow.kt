package pl.wojtek.testutils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Assert.assertEquals

/**
 *
 */


fun <T> Flow<T>.test(scope: CoroutineScope): TestObserver<T> {
    return TestObserver(scope, this)
}

class TestObserver<T>(
    scope: CoroutineScope,
    flow: Flow<T>
) {
    private val values = mutableListOf<T>()
    private val job: Job = scope.launch {
        flow.collect { values.add(it) }
    }

    fun assertNoValues(): TestObserver<T> {
        assertEquals(emptyList<T>(), this.values)
        return this
    }

    fun assertValues(vararg values: T): TestObserver<T> {
        assertEquals(values.toList(), this.values)
        return this
    }

    fun assertValueAt(position: Int, value: T): TestObserver<T> {
        assertEquals(values[position], value)
        return this
    }

    fun finish() {
        job.cancel()
    }
}