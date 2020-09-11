package pl.wojtek.list.domain.favourite

import pl.wojtek.favourites.FavouriteRepository

/**
 *
 */


internal class ChangeFavouriteStatusUseCase(private val favouriteRepository: FavouriteRepository) {
    suspend fun changeFavourite(id: Int) = favouriteRepository.changeMovieStatus(id)
}