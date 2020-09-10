package pl.wojtek.favourites

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

/**
 *
 */


class FavouriteRepository {
    private val favouriteMoviesChannel = ConflatedBroadcastChannel<List<FavouriteMovie>>(emptyList())
    val favouriteMoviesFlow: Flow<List<FavouriteMovie>> get() = favouriteMoviesChannel.asFlow()

    fun loadFavourites(): Flow<List<FavouriteMovie>> = favouriteMoviesFlow
}