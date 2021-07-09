package pl.wojtek.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET


/**
 *
 */


interface GetConfigurationAPI {
    @GET("configuration")
    suspend fun getConfiguration(): Configuration
}


class ImageUrlProvider(private val api: GetConfigurationAPI) {
    private var posterUrl: String? = null
    private var fullSize: String? = null


    init {

    }

    suspend fun providePosterUrl(): String? {
        if (posterUrl == null) {
            loadConfiguration()

        }
        return posterUrl
    }

    private suspend fun loadConfiguration() {
        try {
            val configuration = api.getConfiguration()
            posterUrl = "${configuration.images.secureBaseUrl}${configuration.images.posterSizes[1]}"
            fullSize = "${configuration.images.secureBaseUrl}${configuration.images.posterSizes[configuration.images.posterSizes.size - 2]}"
        } catch (ex: Exception) {

        }
    }

    suspend fun provideFullSizeUrl(): String? {
        if (fullSize == null) {
            try {
                val configuration = api.getConfiguration()
                posterUrl = "${configuration.images.secureBaseUrl}${configuration.images.posterSizes[1]}"
                fullSize = "${configuration.images.secureBaseUrl}${configuration.images.posterSizes[configuration.images.posterSizes.size - 2]}"
            } catch (ex: Exception) {

            }

        }
        return fullSize
    }
}

@JsonClass(generateAdapter = true)
data class Configuration(
    @Json(name = "change_keys")
    val changeKeys: List<String>,
    @Json(name = "images")
    val images: Images
)

@JsonClass(generateAdapter = true)
data class Images(
    @Json(name = "backdrop_sizes")
    val backdropSizes: List<String>,
    @Json(name = "base_url")
    val baseUrl: String,
    @Json(name = "logo_sizes")
    val logoSizes: List<String>,
    @Json(name = "poster_sizes")
    val posterSizes: List<String>,
    @Json(name = "profile_sizes")
    val profileSizes: List<String>,
    @Json(name = "secure_base_url")
    val secureBaseUrl: String,
    @Json(name = "still_sizes")
    val stillSizes: List<String>
)