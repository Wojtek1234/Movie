package pl.wojtek.core

import pl.wojtek.core.common.StringCreator
import pl.wojtek.core.common.StringCreatorImp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 *
 */


val coreModules = listOf(module {
    factory<StringCreator> { StringCreatorImp(androidApplication().resources) }
})