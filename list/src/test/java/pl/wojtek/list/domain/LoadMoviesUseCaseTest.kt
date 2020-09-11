package pl.wojtek.list.domain

import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import mobi.wojtek.pagination.coroutine.CoroutinePaginModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.wojtek.favourites.FavouriteMovie
import pl.wojtek.favourites.FavouriteRepository
import pl.wojtek.list.data.network.Dates
import pl.wojtek.list.data.network.NetworkMovie
import pl.wojtek.list.data.network.NetworkMovieResponse
import pl.wojtek.list.domain.load.LoadMoviesUseCase
import pl.wojtek.list.domain.load.Mapper
import pl.wojtek.list.ui.createMovie
import pl.wojtek.list.ui.createRandomMovies
import pl.wojtek.testutils.MainCoroutineScopeRule
import pl.wojtek.testutils.test

/**
 *
 */
@ExperimentalCoroutinesApi
internal class LoadMoviesUseCaseTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    lateinit var paginModel: CoroutinePaginModel<Unit, NetworkMovie, NetworkMovieResponse>
    lateinit var favouriteRepository: FavouriteRepository
    lateinit var mapper: Mapper

    private lateinit var useCase: LoadMoviesUseCase

    private val favouritesChannel = BroadcastChannel<List<FavouriteMovie>>(1)
    val favouritesFlow: Flow<List<FavouriteMovie>> get() = favouritesChannel.asFlow()

    @Before
    fun setup() {
        paginModel = mockk(relaxed = true)
        favouriteRepository = mockk(relaxed = true)
        mapper = mockk(relaxed = true)

        useCase = LoadMoviesUseCase(paginModel, favouriteRepository, mapper)
        coEvery { paginModel.askForMore() } returns emptyList()
        coEvery { favouriteRepository.loadFavourites() } returns favouritesFlow
    }


    @Test
    fun `when load more lauch data from pagin model`() {
        runBlockingTest {
            //when
            useCase.loadNextPage()
            //then
            coVerify { paginModel.askForMore() }
        }
    }

    @Test
    fun `when asking for loading flow flow from pagin model is passed`() {
        runBlockingTest {
            //given
            val someFlow = flow<Boolean> { }
            coEvery { paginModel.loadingState() } returns someFlow

            //when
            val testFlow = useCase.loading()

            //then
            testFlow shouldBe someFlow
        }
    }

    @Test
    fun `when loaded some movies, no favourites, mapped results are returned`() {
        runBlockingTest {
            //given
            val networkMovies = (0..10).map { networkMovie(it) }
            coEvery { paginModel.askForMore() } returns networkMovies
            val mappedMovies = createRandomMovies()
            coEvery { mapper.map(networkMovies, emptyList()) } returns mappedMovies

            //when

            val testFlow = useCase.loadedMovies().test(coroutineScope)
            favouritesChannel.send(emptyList())
            useCase.loadNextPage()

            //then
            testFlow.assertValues(mappedMovies)
            testFlow.finish()
        }
    }

    @Test
    fun `when loaded some movies, with favourites, mapped results are returned , but map gets favourite list as well`() {
        runBlockingTest {
            //given
            val networkMovies = (0..10).map { networkMovie(it) }
            coEvery { paginModel.askForMore() } returns networkMovies
            val mappedMovies = createRandomMovies()

            val listOfFavourites = listOf(FavouriteMovie(networkMovies[2].id), FavouriteMovie(networkMovies[5].id))
            coEvery { mapper.map(networkMovies, listOfFavourites) } returns mappedMovies

            //when

            val testFlow = useCase.loadedMovies().test(coroutineScope)
            favouritesChannel.send(listOfFavourites)
            useCase.loadNextPage()

            //then
            coVerify { mapper.map(networkMovies, listOfFavourites) }
            testFlow.assertValues(mappedMovies)
            testFlow.finish()
        }
    }

    @Test
    fun `when query, filtered result is provided`() {
        runBlockingTest {
            //given
            val networkMovies = (0..10).map { networkMovie(it) }
            coEvery { paginModel.askForMore() } returns networkMovies
            val mappedMovies = listOf(createMovie(1100), createMovie(1151), createMovie(11512))
            coEvery { mapper.map(networkMovies, emptyList()) } returns mappedMovies

            //when

            val testFlow = useCase.loadedMovies().test(coroutineScope)
            favouritesChannel.send(emptyList())
            useCase.loadNextPage()

            //then
            testFlow.assertValues(mappedMovies)

            //when
            useCase.setFilterQuery("11")
            coroutineScope.dispatcher.advanceTimeBy(10)
            //then
            testFlow.assertValueAt(1,mappedMovies)

            //when
            useCase.setFilterQuery("115")
            coroutineScope.dispatcher.advanceTimeBy(10)
            //then
            testFlow.assertValueAt(2, listOf(mappedMovies[1],mappedMovies[2]))

            //when
            useCase.setFilterQuery("11512")
            coroutineScope.dispatcher.advanceTimeBy(10)
            //then
            testFlow.assertValueAt(3, listOf(mappedMovies[2]))

            //when
            useCase.setFilterQuery("")
            coroutineScope.dispatcher.advanceTimeBy(10)
            //then
            testFlow.assertValueAt(4, mappedMovies)
        }
    }
}

fun networkMovie(it: Int) = NetworkMovie(
    false, "back$it", emptyList(), it, "$it", "orginal", "overview$it",
    12.12 * it, "poster$it", "release$it", "title$it", false, it.toDouble(), it
)

private fun network(movies: List<NetworkMovie> = (0..10).map { networkMovie(it) }) = NetworkMovieResponse(Dates("a", "b"), 1, movies, 123, 123)