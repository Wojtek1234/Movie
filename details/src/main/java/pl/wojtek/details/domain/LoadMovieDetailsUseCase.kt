package pl.wojtek.details.domain

import pl.wojtek.core.ImageUrlProvider
import pl.wojtek.core.common.StringCreator
import pl.wojtek.details.data.LoadMovieDetailsApi
import pl.wojtek.favourites.FavouriteRepository

/**
 *
 */


internal class LoadMovieDetailsUseCase(private val movieId: Int,
                                       private val imageUrlProvider: ImageUrlProvider,
                                       private val api: LoadMovieDetailsApi,
                                       private val stringCreator: StringCreator,
                                       private val favouriteRepository: FavouriteRepository
) {
    suspend fun loadMovieDetails(): MovieDetails {
        val isFavourite = favouriteRepository.isFavourite(movieId)
        val networkMovie = api.getMovieDetails(movieId)
        val photo = "${imageUrlProvider.provideFullSizeUrl()}${networkMovie.posterPath}"
        return MovieDetails(
            networkMovie.title,
            photo,
            stringCreator.createDate(networkMovie.releaseDate),
            networkMovie.overview,
            networkMovie.voteAverage.toString(),
            isFavourite
        )
    }

    suspend fun changeFavourites(): Boolean = favouriteRepository.changeMovieStatus(movieId)

}