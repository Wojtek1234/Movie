package pl.wojtek.movie.navigation

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import pl.wojtek.core.navigate.NavigationState
import pl.wojtek.core.navigate.Navigator

/**
 *
 */
internal data class NavigationJump(val bundle: Bundle?,
                                   val fromState: NavigationState = NavigationState.UNKNOWN,
                                   val transitionViews: Map<String, View>? = null)

internal class NavigationRouter(private val id: Int) : Navigator {

    override suspend fun jumpToNext(from: NavigationState, arguments: Bundle?, transitionViews: Map<String, View>?) =
        navigationJumpProcessor.emit(NavigationJump(arguments, from, transitionViews))

    override suspend fun goBack(from: NavigationState) = goBackProcessorProcessor.emit(from)

    override suspend fun logout() {
        logoutFlowProcessor.emit(Unit)
    }

    override fun getIdOfNavigation(): Int = id

    private val navigationJumpProcessor = MutableSharedFlow<NavigationJump>(replay = 0)
    internal val navigationJumpStream: SharedFlow<NavigationJump> get() = navigationJumpProcessor

    private val goBackProcessorProcessor = MutableSharedFlow<NavigationState>(replay = 0)
    internal val goBackProcessorStream: SharedFlow<NavigationState> get() = goBackProcessorProcessor

    private val logoutFlowProcessor = MutableSharedFlow<Unit>(replay = 0)
    internal val logoutFlowStream: SharedFlow<Unit> get() = logoutFlowProcessor
}