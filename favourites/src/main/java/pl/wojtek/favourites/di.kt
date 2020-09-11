package pl.wojtek.favourites

import org.koin.dsl.module
import pl.wojtek.favourites.db.dbModule

/**
 *
 */


val favouriteModules = module {
    single { FavouriteRepository(get()) }
} + dbModule