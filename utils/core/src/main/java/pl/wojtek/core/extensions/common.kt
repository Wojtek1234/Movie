package pl.wojtek.core.extensions

import pl.wojtek.core.R

inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
    return input?.let(callback)
}


fun getFavouriteIcon(boolean: Boolean) = if (boolean) R.drawable.ic_baseline_star_24 else R.drawable.ic_outline_star_border_24