package pl.wojtek.core

import com.squareup.picasso.Picasso
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.wojtek.core.common.StringCreator
import pl.wojtek.core.common.StringCreatorImp
import pl.wojtek.core.extensions.getApi
import pl.wojtek.core.image.ImageLoader

/**
 *
 */


val coreModules = listOf(module {
    factory<StringCreator> { StringCreatorImp(androidApplication().resources) }
    single { ImageLoader(Picasso.get()) }
    single<ImageUrlProvider> { ImageUrlProvider(getApi()) }
})