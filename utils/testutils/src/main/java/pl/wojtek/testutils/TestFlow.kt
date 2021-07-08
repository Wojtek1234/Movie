package com.agronet.testutils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import kotlin.test.assertTrue

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

    fun assertValuesAt(position: Int, value: T): TestObserver<T> {
        assertEquals(values[position], value)
        return this
    }

    fun assertValuesAt(position: Int, matcher: (T) -> Boolean): TestObserver<T> {
        assertTrue { matcher(values[position]) }
        return this
    }

    fun assertSize(size: Int): TestObserver<T> {
        assertEquals(values.size, size)
        return this
    }

    fun isFinished() {
        assertFalse(job.isActive)
    }

    fun finish() {
        job.cancel()
    }
}