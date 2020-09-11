package pl.wojtek.favourites.db

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 *
 */


val dbModule = module {
    single { FavouriteDb.buildDatabase(androidContext()) }
    factory<FavouriteDao> { get<FavouriteDb>().getFavouriteDao() }
}