package pl.wojtek.list.data.network

import mobi.wojtek.pagination.DataMapper
import mobi.wojtek.pagination.MappedData
import mobi.wojtek.pagination.QueryParams
import mobi.wojtek.pagination.coroutine.CoroutineDataSource
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 */


internal interface LoadMoviesDataSource {
    @GET("movie/now_playing")
    suspend fun getLatestMovies(@Query("page") page: Int): NetworkMovieResponse
}


internal class MoviesDataSource(private val api: LoadMoviesDataSource) : CoroutineDataSource<Unit, NetworkMovieResponse> {
    override suspend fun askForData(query: QueryParams<Unit>): NetworkMovieResponse =
        api.getLatestMovies(page = query.page+1)
}

internal class MoviesNetworkDataMapper : DataMapper<NetworkMovieResponse, NetworkMovie, Unit> {
    override fun map(a: NetworkMovieResponse, q: QueryParams<Unit>): MappedData<Unit, NetworkMovie> {
        return MappedData(Unit, a.networkMovies, a.totalResults)
    }
}