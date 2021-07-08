package pl.immotef.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.agronet.testutils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.wojtek.preferences.PreferencesFacade
import pl.wojtek.preferences.PreferencesFacadeImp
import kotlin.test.assertEquals


/**
 *
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class PreferencesFacadeImpTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()
    lateinit var preferencesFacade: PreferencesFacade
    private val prefsName = "mysharedpref"
    lateinit var preferences: SharedPreferences

    @Before
    fun setup() {

        val context: Context = getInstrumentation().context
        preferences = context.getSharedPreferences(
            prefsName,
            Context.MODE_PRIVATE
        )

        preferencesFacade = PreferencesFacadeImp(preferences)
    }

    @After
    fun tearDown() {
        val editor = preferences.edit()
        editor.clear()
        editor.commit()
    }


    @Test
    fun testSavingPrefs() {
        runBlockingTest {

            //given
            val someFancyString = "tralalal"
            val someFancyKey = "key_value"

            //when
            preferencesFacade.saveString(someFancyString, someFancyKey)

            //then
            assertEquals(preferences.getString(someFancyKey, "some tottaly different value"), someFancyString)
        }
    }

    @Test
    fun testRetrievingPrefs() {
        runBlockingTest {

            //given
            val someFancyString = "tralalal"
            val someFancyKey = "key_value"

            //when
            preferences.edit().putString(someFancyKey,someFancyString).apply()


            //then
            assertEquals(preferencesFacade.retrieveString(someFancyKey), someFancyString)
        }
    }


    @Test
    fun testSavingIntPrefs() {
        runBlockingTest {

            //given
            val someFancyInt = 1234
            val someFancyKey = "key_value"

            //when
            preferencesFacade.saveInt(someFancyInt, someFancyKey)

            //then
            assertEquals(preferences.getInt(someFancyKey, -123), someFancyInt)
        }
    }

    @Test
    fun testRetrievingIntPrefs() {
        runBlockingTest {

            //given
            val someFancyInt = 1234
            val someFancyKey = "key_value"

            //when
            preferences.edit().putInt(someFancyKey,someFancyInt).apply()


            //then
            assertEquals(preferencesFacade.retrieveInt(someFancyKey), someFancyInt)
        }
    }
}