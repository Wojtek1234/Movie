package pl.wojtek.core.extensions

inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
    return input?.let(callback)
}