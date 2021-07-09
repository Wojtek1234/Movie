package pl.wojtek.movie

import com.agronet.testutils.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import pl.wojtek.core.CoroutineUtils
import pl.wojtek.core.navigate.NavigationData
import pl.wojtek.core.navigate.NavigationState
import pl.wojtek.movie.navigation.NavigationDataProvider
import pl.wojtek.movie.navigation.NavigationJump
import pl.wojtek.movie.navigation.NavigationRouter
import pl.wojtek.movie.navigation.NavigationViewModel
import pl.wojtek.testutils.CoroutinesTestExtension
import pl.wojtek.testutils.InstantExecutorExtension


/**
 *
 */
@ExtendWith(InstantExecutorExtension::class)
class NavigationViewModelTest {

    @JvmField
    @RegisterExtension
    val coroutineScope = CoroutinesTestExtension()


    private lateinit var navigationRouter: NavigationRouter
    private lateinit var coroutineUtils: CoroutineUtils
    private lateinit var navigationDataProvider: NavigationDataProvider
    private lateinit var viewModelUnderTest: NavigationViewModel

    private val navigateFlowProcessor = MutableSharedFlow<NavigationJump>(replay = 0)
    private val goBackFlowProcessor = MutableSharedFlow<NavigationState>(replay = 0)

    @BeforeEach
    fun setup() {
        navigationRouter = mockk(relaxed = true)
        navigationDataProvider = mockk(relaxed = true)
        coroutineUtils = mockk(relaxed = true)
        coEvery { coroutineUtils.io } returns coroutineScope.dispatcher
        coEvery { navigationRouter.goBackProcessorStream } returns goBackFlowProcessor
        coEvery { navigationRouter.navigationJumpStream } returns navigateFlowProcessor

        viewModelUnderTest = NavigationViewModel(navigationRouter, coroutineUtils)
    }

    @Test
    fun `when NavigationDataProvider is not set, do not react on go forward event`() {
        runBlockingTest {
            //given
            val navigationJump = NavigationJump(null)

            //when
            val testStream = viewModelUnderTest.navigateToDestinationFlowStream.test(coroutineScope).assertNoValues()
            navigateFlowProcessor.emit(navigationJump)

            //then
            testStream.assertNoValues().finish()
        }
    }

    @Test
    fun `when NavigationDataProvider is not set, do not react on go back event`() {
        runBlockingTest {

            //when
            val testStream = viewModelUnderTest.navigateUpStream.test(coroutineScope).assertNoValues()
            goBackFlowProcessor.emit(NavigationState.UNKNOWN)

            //then
            testStream.assertNoValues().finish()
        }
    }

    @Test
    fun `when NavigationDataProvider is  set, proper event should appear on stream`() {
        runBlockingTest {

            //when
            val testStream = viewModelUnderTest.navigateUpStream.test(coroutineScope).assertNoValues()
            viewModelUnderTest.navigationDataProvider = navigationDataProvider
            goBackFlowProcessor.emit(NavigationState.UNKNOWN)

            //then
            testStream.assertValues(Unit).finish()
        }
    }


    @Test
    fun `when current destination is list , go forward should create proper value`() {
        runBlockingTest {
            //given
            coEvery { navigationDataProvider.getCurrentState() } returns R.id.movieListFragment
            //when
            val testStream = viewModelUnderTest.navigateToDestinationFlowStream.test(coroutineScope).assertNoValues()
            viewModelUnderTest.navigationDataProvider = navigationDataProvider
            navigateFlowProcessor.emit(NavigationJump(null, NavigationState.LIST))

            //then
            testStream.assertValues(NavigationData(R.id.action_movieListFragment_to_movieDetailsFragment, null)).finish()
        }
    }


}