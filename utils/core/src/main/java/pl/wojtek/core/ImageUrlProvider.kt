package pl.wojtek.core

/**
 *
 */


interface ImageUrlProvider{
    suspend fun providePosterUrl():String?
}