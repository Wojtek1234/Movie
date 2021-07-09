package pl.wojtek.network

import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 */



private const val TIMEOUT = 60L

fun networkingModule(url: String, apiKey: String, isDebug: Boolean) = module {

    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }

    single {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
            val url = request.url.newBuilder().addQueryParameter(
                "api_key", apiKey
            ).build()

            val auth = request.newBuilder()
                .let {
                    it.url(url)
                    it.build()
                }
            chain.proceed(auth)
        }
        interceptor
    }

    single<OkHttpClient> {
        OkHttpClient.Builder().apply {
            if (isDebug) {
                addInterceptor(get<HttpLoggingInterceptor>())
            }
            addInterceptor(get<Interceptor>())
        }.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    single {
        MoshiConverterFactory.create(
            Moshi.Builder()
                .add(MoshiDateDeserializer(listOfFormats))
                .build()
        )
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(url)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(get<MoshiConverterFactory>())
            .build()
    }


}

val listOfFormats = listOf("yyyy-MM-dd")





