package pl.wojtek.core

/**
 *
 */


interface ImageUrlProvider{
    suspend fun providePosterUrl():String? = "https://image.tmdb.org/t/p/w154"
}