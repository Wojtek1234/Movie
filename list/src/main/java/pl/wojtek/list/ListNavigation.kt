package pl.wojtek.list

import android.view.View
import android.widget.ImageView
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import pl.wojtek.list.domain.Movie

/**
 *
 */
interface ListNavigationListener {
    fun openMovieDetails(): Flow<Pair<Int, View>>
}


internal class ListNavigation : ListNavigationListener {
    private val openMovieDetailsChannel = BroadcastChannel<Pair<Int, View>>(1)
    private val openMovieDetailsChannelFlow: Flow<Pair<Int, View>> get() = openMovieDetailsChannel.asFlow()
    suspend fun openMovie(movie: Movie, imageView: ImageView) {
        openMovieDetailsChannel.send(movie.id to imageView)
    }

    override fun openMovieDetails(): Flow<Pair<Int, View>> = openMovieDetailsChannelFlow
}