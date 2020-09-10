package pl.wojtek.core.common

/**
 *
 */


interface Mapper<in F, out T> {
    fun map(from: F): T
}