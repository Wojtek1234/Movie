package pl.wojtek.core


import kotlinx.coroutines.CoroutineScope
import pl.wojtek.core.errors.ExceptionWrapperFactory
import kotlin.coroutines.CoroutineContext

/**
 *
 */


interface CoroutineUtils : ExceptionWrapperFactory {
    val main: CoroutineContext
    val io: CoroutineContext
    val computation: CoroutineContext
    val globalScope: CoroutineScope

}


