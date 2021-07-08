package mobi.wojtek.pagination

import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals


/**
 *
 */
class Test(val test: String)
class DataHolderImpTest {

    private lateinit var dataHolder: DataHolder<String, Test>


    @BeforeEach
    fun beforeTest() {
        dataHolder = DataHolderImp()
    }

    @org.junit.jupiter.api.Test
    fun `test if not setted query before returns the same list`() {
        //given
        val list = listOf(Test("testowane"))
        val query = "szukana"
        //when then
        assertEquals(dataHolder.provideData(query, list), list)
    }

    @org.junit.jupiter.api.Test
    fun `test that when different query provided same list is returned`() {
        //given
        val list1 = listOf(Test("test1"))
        val list2 = listOf(Test("test2"))
        val query1 = "szukam1"
        val query2 = "szukam2"

        //when
        dataHolder.provideData(query1, list1)

        //then
        assertEquals(dataHolder.provideData(query2, list2), list2)
    }

    @org.junit.jupiter.api.Test
    fun `test when the same quuery many times then return sum of lists `() {
        //given
        val list1 = listOf(Test("test1"))
        val list2 = listOf(Test("test2"))
        val query1 = "szukam1"

        //when
        dataHolder.provideData(query1, list1)

        //then
        assertEquals(dataHolder.provideData(query1, list2), list1 + list2)
    }

}