package pl.wojtek.movie


import android.app.Application
import mobi.wojtek.pagination.coroutinePaginationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import pl.wojtek.core.coreModules
import pl.wojtek.details.ui.detailsModule
import pl.wojtek.favourites.favouriteModules
import pl.wojtek.list.moviesModules
import pl.wojtek.movie.BuildConfig.API_KEY
import pl.wojtek.movie.BuildConfig.API_URL
import pl.wojtek.network.networkingModule

/**
 *
 */


class Application : Application() {

    private val apiUrl = API_URL
    private val apiKey = API_KEY

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@Application)
            modules(
                listOf(
                    appModule(), networkingModule(apiUrl, apiKey, BuildConfig.DEBUG), coroutinePaginationModule, detailsModule

                )
                        + coreModules + moviesModules + favouriteModules
            )
        }
    }
}