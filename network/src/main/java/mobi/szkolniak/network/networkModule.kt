package mobi.szkolniak.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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




}





