package pl.wojtek.movie.navigation


import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.wojtek.core.navigate.Navigator
import pl.wojtek.movie.R

/**
 *
 */
val navigationDi = module {
    viewModel { NavigationViewModel(get(), get()) }
    single { NavigationRouter(R.id.navHostFragment) }
    factory<Navigator> { get<NavigationRouter>() }
}