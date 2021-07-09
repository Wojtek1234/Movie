package pl.wojtek.core.navigate

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.base.BaseViewModel

/**
 *
 */
class NavigationViewModel(private val navigator: Navigator, coroutineUtils: CoroutineUtils) : BaseViewModel(coroutineUtils) {
    fun logout() {
        viewModelScope.launch {
            navigator.logout()
        }
    }
}