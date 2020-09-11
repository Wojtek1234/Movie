package pl.wojtek.core

import com.google.gson.annotations.SerializedName
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


data class Configuration(
    @SerializedName("change_keys")
    val changeKeys: List<String>,
    @SerializedName("images")
    val images: Images
)

data class Images(
    @SerializedName("backdrop_sizes")
    val backdropSizes: List<String>,
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("logo_sizes")
    val logoSizes: List<String>,
    @SerializedName("poster_sizes")
    val posterSizes: List<String>,
    @SerializedName("profile_sizes")
    val profileSizes: List<String>,
    @SerializedName("secure_base_url")
    val secureBaseUrl: String,
    @SerializedName("still_sizes")
    val stillSizes: List<String>
)