package pl.wojtek.favourites.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 *
 */


@Dao
interface FavouriteDao {
    @Query("SELECT * FROM $fav WHERE id = :id LIMIT 1")
    suspend fun getFavourite(id: Int): Favourite?

    @Query("SELECT * FROM $fav")
    fun getFavourites(): Flow<List<Favourite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: Favourite)

    @Query("DELETE  FROM $fav WHERE id = :id")
    suspend fun deleteFavourite(id: Int)
}