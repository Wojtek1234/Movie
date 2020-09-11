package pl.wojtek.list

import org.koin.dsl.module
import pl.wojtek.list.ui.movieListModule

/**
 *
 */


val moviesModules = listOf(movieListModule, module {
    single { ListNavigation() }
    factory<ListNavigationListener> { get<ListNavigation>() }
})