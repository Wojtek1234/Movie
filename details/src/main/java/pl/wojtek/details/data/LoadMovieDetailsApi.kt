package pl.wojtek.details.data

import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 */


internal interface LoadMovieDetailsApi {
    @GET("movie/{id}")
    suspend fun getMovieDetails(@Path("id") movieId: Int): MovieDetailsNetworkResponse
}
