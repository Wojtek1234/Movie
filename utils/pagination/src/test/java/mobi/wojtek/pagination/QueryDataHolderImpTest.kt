package mobi.wojtek.pagination

import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.random.Random


/**
 *
 */
internal class QueryDataHolderImpTest : StringSpec() {

    private val pageSize = 5
    private lateinit var queryHolder: QueryDataHolder<String>

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        queryHolder = QueryDataHolderImp(pageSize)
    }

    init {
        "test when no query is set ask for more returns false"{
            //when then
            queryHolder.canAskForAnotherOne() shouldBe false
        }

        "test that when query is set can ask for another is true"{
            //given
            val query = "szukam"

            //when
            queryHolder.setQuery(query)

            //then
            queryHolder.canAskForAnotherOne() shouldBe true

        }

        "test when max is set can ask for another is false when exceed limit"{

            //given
            val timesToAsk = 3
            val query = "szukam"
            val max = pageSize * timesToAsk

            //when
            queryHolder.setQuery(query)
            queryHolder.setMax(query, max)
            (0 until timesToAsk).forEach { _ -> queryHolder.turnToNextPage() }

            //then
            queryHolder.canAskForAnotherOne() shouldBe false
        }

        "test when max is set can ask for another is true before exceeding the limit"{
            //given
            val timesToAsk = 3
            val query = "szukam"
            val max = pageSize * timesToAsk

            //when
            queryHolder.setQuery(query)
            queryHolder.setMax(query, max)
            (0 until timesToAsk - 1).forEach { _ -> queryHolder.turnToNextPage() }

            //then
            queryHolder.canAskForAnotherOne() shouldBe true
        }

        "test when max is not multiplied page size can ask for page before exceeding max returns true"{
            //given
            val timesToAsk = 3
            val query = "szukam"
            val max = pageSize * timesToAsk + 2

            //when
            queryHolder.setQuery(query)
            queryHolder.setMax(query, max)
            (0 until timesToAsk).forEach { _ -> queryHolder.turnToNextPage() }

            //then
            queryHolder.canAskForAnotherOne() shouldBe true
        }

        "test when max is not multiplied page size can ask for page after exceeding max returns false"{
            //given
            val timesToAsk = 3
            val query = "szukam"
            val max = pageSize * timesToAsk + 2

            //when
            queryHolder.setQuery(query)
            queryHolder.setMax(query, max)
            (0 until timesToAsk + 1).forEach { _ -> queryHolder.turnToNextPage() }

            //then
            queryHolder.canAskForAnotherOne() shouldBe false
        }

        "test provide query params after first setting the query"{
            //given
            val query = "szukam"

            //when
            queryHolder.setQuery(query)

            //then
            queryHolder.provideQueryParams() shouldBe QueryParams(query, 0, pageSize)
        }

        "test provide query params after turning the page some number of times"{
            //given
            val query = "szukam"
            val numberOfPageTurn = Random.nextInt(1,100)
            //when
            queryHolder.setQuery(query)
            (0 until numberOfPageTurn).forEach { _ -> queryHolder.turnToNextPage() }

            //then
            queryHolder.provideQueryParams() shouldBe QueryParams(query, numberOfPageTurn, pageSize)
        }

        "test that after changing query query page is set back to 0"{
            //given
            val query = "szukam"
            val secondTottalyDifferentQuery = "szukam2"

            //when
            queryHolder.setQuery(query)
            queryHolder.turnToNextPage()
            queryHolder.setQuery(secondTottalyDifferentQuery)

            //then
            queryHolder.provideQueryParams() shouldBe QueryParams(secondTottalyDifferentQuery, 0, pageSize)
        }

        "test that setting the same query as previous does not have any consequences"{
            //given
            val query = "szukam"

            //when
            queryHolder.setQuery(query)
            queryHolder.turnToNextPage()
            queryHolder.setQuery(query)

            //then
            queryHolder.provideQueryParams() shouldBe QueryParams(query, 1, pageSize)
        }

        "test when changing query max is cleared"{
            //given
            val timesToAsk = 3
            val query = "szukam"
            val query2 = "szukam2"
            val max = pageSize * timesToAsk + 2

            //when
            queryHolder.setQuery(query)
            queryHolder.setMax(query, max)
            queryHolder.setQuery(query2)
            (0 until timesToAsk + 1).forEach { _ -> queryHolder.turnToNextPage() }

            //then
            queryHolder.canAskForAnotherOne() shouldBe true
        }
    }
}