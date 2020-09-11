package pl.wojtek.favourites.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *
 */


private const val DATABASE_NAME = "favourite-movie-db"

@Database(entities = [Favourite::class], version = 1, exportSchema = false)
internal abstract class FavouriteDb : RoomDatabase() {
    abstract fun getFavouriteDao(): FavouriteDao

    companion object {
        fun buildDatabase(context: Context): FavouriteDb {
            return Room
                .databaseBuilder(context, FavouriteDb::class.java, DATABASE_NAME)
                .build()
        }
    }
}