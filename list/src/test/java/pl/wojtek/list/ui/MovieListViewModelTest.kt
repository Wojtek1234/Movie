package pl.wojtek.list.ui

import com.agronet.testutils.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.common.ConsumableValue
import pl.wojtek.core.errors.ErrorWrapper
import pl.wojtek.list.domain.Movie
import pl.wojtek.list.domain.favourite.ChangeFavouriteStatusUseCase
import pl.wojtek.list.domain.load.LoadMoviesUseCase
import pl.wojtek.list.presentation.MovieListViewModel
import pl.wojtek.testutils.CoroutinesTestExtension
import pl.wojtek.testutils.InstantExecutorExtension
import kotlin.random.Random

/**
 *
 */
internal fun createRandomMovies()=  (0..Random.nextInt(20)).map { createMovie(it) }

internal fun createMovie(it: Int) = Movie("title$it", "image$it", it % 2 == 0, it)

@ExtendWith(InstantExecutorExtension::class)
internal class MovieListViewModelTest {

    @JvmField
    @RegisterExtension
    val coroutineScope = CoroutinesTestExtension()

    private lateinit var useCase: LoadMoviesUseCase
    private lateinit var changeFavouriteUseCase: ChangeFavouriteStatusUseCase

    private lateinit var coroutineUtils: CoroutineUtils
    private val testDispatcher = TestCoroutineDispatcher()


    private lateinit var viewModel: MovieListViewModel


    private val moviesChannel = BroadcastChannel<List<Movie>>(1)
    private val moviesFlow: Flow<List<Movie>> get() = moviesChannel.asFlow()

    private val loadingChannel = BroadcastChannel<Boolean>(1)
    private val loadingFlow: Flow<Boolean> get() = loadingChannel.asFlow()

    @BeforeEach
    fun setUp() {
        useCase = mockk(relaxed = true)
        coroutineUtils = mockk()
        changeFavouriteUseCase = mockk(relaxed = true)
        coEvery { coroutineUtils.io } returns testDispatcher
        coEvery { useCase.loadedMovies() } returns moviesFlow
        coEvery { useCase.loading() } returns loadingFlow
    }


    @Test
    fun `on start trigger method of loading from use case`() {
        runBlockingTest {
            //when
            viewModel = createViewModel()
            //then
            coVerify {
                useCase.loadedMovies()
                useCase.loadNextPage()
                useCase.loading()
            }
        }
    }

    private fun createViewModel(): MovieListViewModel {
        val vm = MovieListViewModel(useCase, changeFavouriteUseCase, coroutineUtils)
        testDispatcher.advanceTimeBy(500)
        return vm
    }

    @Test
    fun `on load more trigger proper method of use case`() {
        runBlockingTest {
            //given
            viewModel = createViewModel()

            //when
            viewModel.loadMore()

            //then
            coVerifySequence {
                useCase.loading()
                useCase.loadedMovies()
                useCase.loadNextPage()
                useCase.loadNextPage()
            }
        }
    }


    @Test
    fun `when data from use case appears on live data from view model `() {
        runBlockingTest {
            //given
            viewModel = createViewModel()
            val listOfMovies = createRandomMovies()
            val tottalyDifferentListOfMovies = createRandomMovies()

            //when
            val testStream = viewModel.moviesStream.test()
            moviesChannel.send(listOfMovies)
            moviesChannel.send(tottalyDifferentListOfMovies)

            //then
            testStream
                .assertValues(listOfMovies, tottalyDifferentListOfMovies)
                .finish()
        }
    }

    @Test
    fun `when ask for more from use case throws exception proper value appears on error stream`() {
        runBlockingTest {
            //given
            val exceptionToThrow = Exception
            val producedErrorWrapper = ErrorWrapper("title", "text")
            coEvery { coroutineUtils.produce(exceptionToThrow) } returns producedErrorWrapper
            coEvery { useCase.loadNextPage() } throws exceptionToThrow


            //when //then
            viewModel = createViewModel()
            viewModel.errorWrapperStream
                .test()
                .assertValues(ConsumableValue(producedErrorWrapper))
                .finish()
        }
    }

    object Exception : Throwable()


    @Test
    fun `test loading stream from useCase appears on proper liveData`() {
        runBlockingTest {
            //given
            viewModel = createViewModel()

            //when
            val testProgress = viewModel.showProgressStream.test().assertValues(false)
            loadingChannel.send(true)

            //then
            testProgress.assertValuesAt(1, true)

            //when
            loadingChannel.send(false)
            //then
            testProgress.assertValuesAt(2, false)
        }
    }

    @Test
    fun `when setting filter query trigger proper method of use case`() {
        runBlockingTest {
            //given
            viewModel = createViewModel()

            //when
            viewModel.setFilterQuery("query")

            //then
            coVerify { useCase.setFilterQuery("query") }
        }
    }

    @Test
    fun `when changing status of favourite proper method of use case is triggered`() {
        runBlockingTest {
            //given
            viewModel = createViewModel()

            //when
            viewModel.changeMovieFavouriteStatus(createMovie(123))

            //then
            coVerify { changeFavouriteUseCase.changeFavourite(123) }
        }
    }


}