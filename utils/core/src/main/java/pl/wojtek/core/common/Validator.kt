package pl.wojtek.core.common

/**
 *
 */


interface Validator<T> {
    fun validate(t: T): Boolean
}