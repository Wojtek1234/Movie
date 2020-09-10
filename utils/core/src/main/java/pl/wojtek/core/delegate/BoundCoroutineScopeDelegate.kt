package pl.wojtek.core.delegate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.reflect.KProperty

class BoundCoroutineScopeDelegate {
    operator fun getValue(thisRef: Any, property: KProperty<*>): CoroutineScope {
        return CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

}