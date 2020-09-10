package pl.wojtek.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch

/**
 *
 */


fun <T> BroadcastChannel<T>.sendInScope(scope: CoroutineScope, value: T) {
    scope.launch {
        this@sendInScope.send(value)
    }
}