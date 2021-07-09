package pl.wojtek.list.presentation

import android.os.Bundle
import android.widget.ImageView
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.base.BaseViewModel
import pl.wojtek.core.navigate.NavigationState
import pl.wojtek.core.navigate.Navigator

/**
 *
 */
internal class MoveToDetailsViewModel(private val navigator: Navigator,
                                      coroutineUtils: CoroutineUtils,
                                      private val currentDestination: NavigationState = NavigationState.LIST)
    : BaseViewModel(coroutineUtils) {


    fun moveToDetails(movieBundle: Bundle, imageView: ImageView) {
        launchWithProgress {
            navigator.jumpToNext(currentDestination, arguments = movieBundle, mapOf(imageView.transitionName to imageView))

        }
    }
}