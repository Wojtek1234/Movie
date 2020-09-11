package pl.wojtek.favourites.db

import android.content.Context
import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import io.kotlintest.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.wojtek.testutils.MainCoroutineScopeRule
import pl.wojtek.testutils.test
import java.io.IOException

/**
 *
 */

@RunWith(AndroidJUnit4ClassRunner::class)
internal class FavouriteDaoTest {


    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()


    private lateinit var favDao: FavouriteDao
    private lateinit var db: FavouriteDb

    private val id1 = 123
    private val id2 = 2012

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        db = Room.inMemoryDatabaseBuilder(context, FavouriteDb::class.java).build()
        favDao = db.getFavouriteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun whenThereIsNoElementsInDbEmptyFlowIsReturn() {
        runBlocking {
            //when then
            favDao.getFavourites().test(coroutineScope).assertValues(emptyList()).finish()
        }
    }


    @Test
    fun whenAddedFavouriteItAppearsOnFlow() {
        runBlocking {
            //given
            val fav = Favourite(id1)
            //when
            val testFlow = favDao.getFavourites().test(coroutineScope)

            favDao.insertFavourite(fav)
            delay(100)
            //then
            testFlow.assertValueAt(1, listOf(fav))
            testFlow.finish()
        }
    }

    @Test
    fun whenDeleteFavouriteItDisappearsFromFlow() {
        runBlocking {
            //given
            val fav = Favourite(id1)
            //when
            val testFlow = favDao.getFavourites().test(coroutineScope)

            favDao.insertFavourite(fav)
            delay(100)
            favDao.deleteFavourite(fav.id)
            delay(100)
            //then
            testFlow.assertValueAt(2, emptyList())
            testFlow.finish()
        }
    }

    @Test
    fun returnNullWhenThereIsNoFavouriteInDB() {
        runBlocking {
            //when then
            favDao.getFavourite(id1) shouldBe null
        }
    }

    @Test
    fun whenFavouriteExistReturnIt() {
        runBlocking {
            //given
            val fav = Favourite(id1)
            //when
            favDao.insertFavourite(fav)
            val favFromDb = favDao.getFavourite(fav.id)

            //then
            favFromDb shouldBe fav
        }
    }
}