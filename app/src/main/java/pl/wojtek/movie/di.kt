package pl.wojtek.movie

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.wojtek.core.CoroutineUtils


/**
 *
 */

private const val sharedPrefsName = "AGRO_PREFS"

fun appModule() = module {
    single<SharedPreferences> { androidContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE) }
    single<CoroutineUtils> { CoroutineUtilsImp() }
}