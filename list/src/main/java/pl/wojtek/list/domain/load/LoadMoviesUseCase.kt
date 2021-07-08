package pl.wojtek.list.domain.load

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import mobi.wojtek.pagination.coroutine.CoroutinePaginModel
import pl.wojtek.core.delegate.BoundCoroutineScopeDelegate
import pl.wojtek.favourites.FavouriteRepository
import pl.wojtek.list.data.network.NetworkMovie
import pl.wojtek.list.data.network.NetworkMovieResponse
import pl.wojtek.list.domain.Movie

/**
 *
 */


internal class LoadMoviesUseCase(
    private val paginModel: CoroutinePaginModel<Unit, NetworkMovie, NetworkMovieResponse>,
    private val favouriteRepository: FavouriteRepository,
    private val mapper: Mapper
) {
    private val queryChannel = MutableStateFlow<String>("")
    private val loadMoviesChannel = MutableSharedFlow<List<NetworkMovie>>(1)

    private val scope by BoundCoroutineScopeDelegate()

    init {
        scope.launch {
            paginModel.setQuery(Unit)
        }
    }

    private val loadMoviesFlow: Flow<List<Movie>>
        get() = loadMoviesChannel
            .combine(favouriteRepository.loadFavourites())
            { results, favourites ->
                mapper.map(results, favourites)
            }
            .combine(queryChannel)
            { movies, query ->
                if (query.isBlank()) movies
                else
                    movies.filter { it.title.lowercase().contains(query.lowercase()) }
            }

    suspend fun loadNextPage() {
        paginModel.askForMore()?.let {
            loadMoviesChannel.emit(it)
        }
    }

    fun loadedMovies(): Flow<List<Movie>> = loadMoviesFlow
    fun loading(): Flow<Boolean> = paginModel.loadingState()

    suspend fun setFilterQuery(query: String) {
        queryChannel.emit(query)
    }

    fun clear() {
        paginModel.clear()
    }
}