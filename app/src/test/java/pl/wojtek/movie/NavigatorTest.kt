package pl.wojtek.movie

import android.os.Bundle
import com.agronet.testutils.test
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import pl.wojtek.core.navigate.NavigationState
import pl.wojtek.movie.navigation.NavigationJump
import pl.wojtek.movie.navigation.NavigationRouter
import pl.wojtek.testutils.CoroutinesTestExtension


/**
 *
 */
class NavigatorTest {

    @JvmField
    @RegisterExtension
    val coroutineScope = CoroutinesTestExtension()

    private lateinit var routerUnderTest: NavigationRouter

    @BeforeEach
    fun setUp() {
        routerUnderTest = NavigationRouter(123)
    }

    @Test
    fun `when jumping without arguments, just empty navigation data appears on stream`() {
        runBlockingTest {
            //given
            testNavigateToStream(null)
        }
    }

    @Test
    fun `when jumping with arguments, proper data should appear on stream`() {
        runBlockingTest {
            //given
            val bundle = mockk<Bundle>(relaxed = true)
            testNavigateToStream(bundle)
        }
    }

    @Test
    fun `when goBack proper event should appear on the stream`() {
        runBlockingTest {
            //given
            val current = NavigationState.UNKNOWN
            //when
            val testStream = routerUnderTest.goBackProcessorStream.test(coroutineScope).assertNoValues()
            routerUnderTest.goBack(current)

            //then
            testStream.assertValues(current).finish()
        }
    }

    private suspend fun testNavigateToStream(arguments: Bundle?) {
        //given
        val current = NavigationState.UNKNOWN

        //when
        val testStream = routerUnderTest.navigationJumpStream.test(coroutineScope).assertNoValues()
        routerUnderTest.jumpToNext(current, arguments)

        //then
        testStream.assertValues(NavigationJump(arguments, current)).finish()
    }


}