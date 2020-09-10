package pl.wojtek.movie

import android.content.Intent

import kotlinx.coroutines.*
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.errors.ErrorWrapper
import retrofit2.HttpException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

/**
 *
 */


internal class CoroutineUtilsImp() : CoroutineUtils {
    override val main: CoroutineContext = Dispatchers.Main
    override val io: CoroutineContext = Dispatchers.IO

    @ObsoleteCoroutinesApi
    override val computation: CoroutineContext = newFixedThreadPoolContext(4, "Computation dispatcher")
    override val globalScope: CoroutineScope = GlobalScope
    override fun produce(throwable: Throwable?): ErrorWrapper {

        return when (throwable) {
            is HttpException -> ErrorWrapper("Error", "Some internet connection error" )
            is UnknownHostException -> ErrorWrapper("Error", "Check your internet connection" )
            else -> ErrorWrapper("Exception", throwable?.message ?: throwable?.cause?.localizedMessage?:"" )
        }
    }
}