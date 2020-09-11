package pl.wojtek.list.domain.filter

import pl.wojtek.list.data.network.LoadSearchHintsApi

/**
 *
 */


internal class FilterMoviesUseCase(private val api: LoadSearchHintsApi) {
    suspend fun provideFilterHints(query: String): List<FilterHint> = if (query.isNotBlank()) {
        api.loadMovies(query).networkMovies.map { FilterHint(it.title, it.id) }
    } else {
        emptyList()
    }
}