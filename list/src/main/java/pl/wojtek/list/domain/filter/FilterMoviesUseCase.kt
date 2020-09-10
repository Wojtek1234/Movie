package pl.wojtek.list.domain.filter

/**
 *
 */


internal class FilterMoviesUseCase() {
    suspend fun provideFilterHints(query: String): List<FilterHint> = emptyList()
}