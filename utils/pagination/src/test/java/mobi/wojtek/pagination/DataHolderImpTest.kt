package mobi.wojtek.pagination

import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 *
 */
class Test(val test:String)
class DataHolderImpTest:StringSpec(){

    private lateinit var dataHolder:DataHolder<String,Test>


    override fun beforeTest(testCase: TestCase) {
        dataHolder = DataHolderImp()
    }

    init {
        "test if not setted query before returns the same list"{
            //given
            val list = listOf(Test("testowane"))
            val query = "szukana"
            //when then
            dataHolder.provideData(query,list) shouldBe list
        }

        "test that when different query provided same list is returned"{
            //given
            val list1 = listOf(Test("test1"))
            val list2 = listOf(Test("test2"))
            val query1 = "szukam1"
            val query2 = "szukam2"

            //when
            dataHolder.provideData(query1,list1)

            //then
            dataHolder.provideData(query2,list2) shouldBe list2
        }

        "test when the same quuery many times then return sum of lists "{
            //given
            val list1 = listOf(Test("test1"))
            val list2 = listOf(Test("test2"))
            val query1 = "szukam1"

            //when
            dataHolder.provideData(query1,list1)

            //then
            dataHolder.provideData(query1,list2) shouldBe list1 + list2
        }
    }
}