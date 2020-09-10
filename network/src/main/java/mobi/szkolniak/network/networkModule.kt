package mobi.szkolniak.network

import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 */



private const val TIMEOUT = 60L

const val WITH_CLIENT = "with_client"

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

    val certificatePinner = CertificatePinner.Builder()
        .add(
            "*.szkolniak.mobi",
            "sha256/hTkey8ep9HlLSAsVwMLzAYtWdBMhK3P9//wFNYvVqcU="
        ).build()

    single<OkHttpClient> {
        OkHttpClient.Builder().certificatePinner(certificatePinner).apply {
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
        GsonConverterFactory.create(
            GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .setLenient()
                .create()
        )
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(url)
            .client(get())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(get<GsonConverterFactory>())
            .build()
    }

    factory<RetrofitProvider> {
        (
                object : RetrofitProvider {
                    override val url: String = url
                    override val converterFactory: Converter.Factory = get<GsonConverterFactory>()
                })
    }


}

interface RetrofitProvider {
    val url: String
    val converterFactory: Converter.Factory
    fun createRetrofit(builder: OkHttpClient.Builder): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(builder.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
}

inline fun <reified T> Scope.getApi(okHttpClient: OkHttpClient.Builder): T = get<RetrofitProvider>().createRetrofit(okHttpClient).create(T::class.java)



