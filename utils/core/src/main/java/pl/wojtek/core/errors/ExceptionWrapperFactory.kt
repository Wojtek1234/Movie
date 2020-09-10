package pl.wojtek.core.errors

/**
 *
 */


interface ExceptionWrapperFactory {
    fun produce(throwable: Throwable?): ErrorWrapper
}