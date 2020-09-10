package pl.wojtek.list.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *
 */


internal class LoadMoviesUseCase() {
    suspend fun loadNextPage() {}
    fun loadedMovies(): Flow<List<Movie>> = flow {}
    fun loading(): Flow<Boolean> = flow { }
}