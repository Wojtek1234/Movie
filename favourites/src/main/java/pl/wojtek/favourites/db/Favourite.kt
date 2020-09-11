package pl.wojtek.favourites.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 */
const val fav = "favourite_table"

@Entity(tableName = fav)
data class Favourite(@PrimaryKey(autoGenerate = false) val id: Int)