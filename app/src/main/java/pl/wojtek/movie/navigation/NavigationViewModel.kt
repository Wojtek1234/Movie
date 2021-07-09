package pl.wojtek.movie.navigation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.base.BaseViewModel
import pl.wojtek.core.navigate.NavigationData
import pl.wojtek.core.navigate.NavigationState
import pl.wojtek.movie.R

/**
 *
 */

internal class NavigationViewModel(private val navigationRouter: NavigationRouter,
                                   coroutineUtils: CoroutineUtils) : BaseViewModel(coroutineUtils) {
    private val navigateToDestinationFlowProcessor = MutableSharedFlow<NavigationData>(replay = 0)
    val navigateToDestinationFlowStream: SharedFlow<NavigationData> get() = navigateToDestinationFlowProcessor

    private val logoutFlowProcessor = MutableSharedFlow<Unit>(replay = 0)
    val logoutStream: SharedFlow<Unit> get() = logoutFlowProcessor

    val navigateUpStream: Flow<Unit> get() = navigationRouter.goBackProcessorStream.filter { navigationDataProvider != null }.map { }

    var navigationDataProvider: NavigationDataProvider? = null


    init {
        viewModelScope.launch {
            navigationRouter.navigationJumpStream.collect {
                when {
                    navigationDataProvider?.getCurrentState() == null -> {
                    }

                    it.fromState == NavigationState.LIST && checkIsCurrentState(R.id.movieListFragment) -> {
                        navigateToDestinationFlowProcessor.emit(
                            NavigationData(
                                R.id.action_movieListFragment_to_movieDetailsFragment,
                                it.bundle,
                                it.transitionViews
                            )
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            navigationRouter.logoutFlowStream.collect {
                logoutFlowProcessor.emit(Unit)
            }
        }
    }

    private fun checkIsCurrentState(state: Int) = navigationDataProvider?.getCurrentState() == state
}