package pl.wojtek.favourites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.wojtek.favourites.db.Favourite
import pl.wojtek.favourites.db.FavouriteDao

/**
 *
 */


class FavouriteRepository(private val dao: FavouriteDao) {

    fun loadFavourites(): Flow<List<FavouriteMovie>> = dao.getFavourites().map { dbs -> dbs.map { en -> FavouriteMovie(en.id) } }

    suspend fun changeMovieStatus(id: Int) {
        if (dao.getFavourite(id) != null) {
            dao.deleteFavourite(id)
        } else {
            dao.insertFavourite(Favourite(id))
        }
    }

    suspend fun isFavourite(id: Int): Boolean = dao.getFavourite(id) != null
}