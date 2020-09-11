package pl.wojtek.list.data.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 */


internal interface LoadSearchHintsApi {
    @GET("search/movie")
    suspend fun loadMovies(@Query("query") query: String): NetworkMovieResponse
}