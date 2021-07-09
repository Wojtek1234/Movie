package pl.wojtek.core.navigate

import android.os.Bundle
import android.view.View

/**
 *
 */

interface Navigator {
    suspend fun jumpToNext(from: NavigationState, arguments: Bundle? = null, transitionViews: Map<String, View>? = null)
    suspend fun goBack(from: NavigationState)
    suspend fun logout()
    fun getIdOfNavigation(): Int = -1
}

sealed class NavigationState {
    object UNKNOWN : NavigationState()
    object LIST : NavigationState()
    object Details : NavigationState()
}


data class NavigationData(val action: Int,
                          val bundle: Bundle? = null,
                          val transitionView: Map<String, View>? = null)





