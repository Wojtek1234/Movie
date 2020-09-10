package pl.wojtek.list.domain

import pl.wojtek.core.ImageUrlProvider
import pl.wojtek.favourites.FavouriteMovie
import pl.wojtek.list.data.network.NetworkMovie

/**
 *
 */


internal class Mapper(private val imageUrlProvider: ImageUrlProvider) {

    suspend fun map(list: List<NetworkMovie>, favourites: List<FavouriteMovie>) = list.map {
        Movie(it.title, getPosterFullPath(it.posterPath), favourites.any { fav -> fav.id == it.id }, it.id)
    }

    private suspend fun getPosterFullPath(posterPath: String): String? {
        val firstPath = imageUrlProvider.providePosterUrl() ?: return null
        return "$firstPath$posterPath"
    }
}