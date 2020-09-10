package pl.wojtek.preferences

import org.koin.dsl.module

/**
 *
 */


val preferencesModule = module {
    single<PreferencesFacade> { PreferencesFacadeImp(get()) }
}