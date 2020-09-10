package pl.wojtek.favourites

import org.koin.dsl.module

/**
 *
 */



val favouriteModule = module {
    single { FavouriteRepository() }
}