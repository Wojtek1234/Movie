package pl.wojtek.list.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import mobi.wojtek.pagination.coroutine.CoroutinePaginModel
import pl.wojtek.core.delegate.BoundCoroutineScopeDelegate
import pl.wojtek.favourites.FavouriteRepository
import pl.wojtek.list.data.network.NetworkMovie
import pl.wojtek.list.data.network.NetworkMovieResponse

/**
 *
 */


internal class LoadMoviesUseCase(
    private val paginModel: CoroutinePaginModel<Unit, NetworkMovie, NetworkMovieResponse>,
    private val favouriteRepository: FavouriteRepository,
    private val mapper: Mapper
) {
    private val queryChannel = ConflatedBroadcastChannel<String>("")
    private val loadMoviesChannel = BroadcastChannel<List<NetworkMovie>>(1)

    private val scope by BoundCoroutineScopeDelegate()

    init {
        scope.launch{
            paginModel.setQuery(Unit)
        }

    }

    private val loadMoviesFlow: Flow<List<Movie>>
        get() = loadMoviesChannel.asFlow()
            .combine(favouriteRepository.loadFavourites())
            { results, favourites ->
                mapper.map(results, favourites)
            }
            .combine(queryChannel.asFlow())
            { movies, query ->
                if (query.isBlank()) movies
                else
                    movies.filter { it.title.toLowerCase().contains(query.toLowerCase()) }
            }

    suspend fun loadNextPage() {
        paginModel.askForMore()?.let {
            loadMoviesChannel.send(it)
        }
    }

    fun loadedMovies(): Flow<List<Movie>> = loadMoviesFlow
    fun loading(): Flow<Boolean> = paginModel.loadingState()

    suspend fun setFilterQuery(query: String) {
        queryChannel.send(query)
    }

    fun clear() {
        queryChannel.cancel()
        paginModel.clear()
        loadMoviesChannel.cancel()
    }
}