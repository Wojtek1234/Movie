package mobi.wojtek.pagination

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals


/**
 *
 */
internal class QueryDataHolderImpTest {

    private val pageSize = 5
    private lateinit var queryHolder: QueryDataHolder<String>

    @BeforeEach
    fun beforeTest() {
        queryHolder = QueryDataHolderImp(pageSize)
    }

    @Test
    fun `test when no query is set ask for more returns false`() {
        //when then
        assertEquals(queryHolder.canAskForAnotherOne(), false)
    }

    @Test
    fun `test that when query is set can ask for another is true`() {
        //given
        val query = "szukam"

        //when
        queryHolder.setQuery(query)

        //then
        assertEquals(queryHolder.canAskForAnotherOne(), true)

    }

    @Test
    fun `test when max is set can ask for another is false when exceed limit`() {

        //given
        val timesToAsk = 3
        val query = "szukam"
        val max = pageSize * timesToAsk

        //when
        queryHolder.setQuery(query)
        queryHolder.setMax(query, max)
        (0 until timesToAsk).forEach { _ -> queryHolder.turnToNextPage() }

        //then
        assertEquals(queryHolder.canAskForAnotherOne(), false)
    }

    @Test
    fun `test when max is set can ask for another is true before exceeding the limit`() {
        //given
        val timesToAsk = 3
        val query = "szukam"
        val max = pageSize * timesToAsk

        //when
        queryHolder.setQuery(query)
        queryHolder.setMax(query, max)
        (0 until timesToAsk - 1).forEach { _ -> queryHolder.turnToNextPage() }

        //then
        assertEquals(queryHolder.canAskForAnotherOne(), true)
    }

    @Test
    fun `test when max is not multiplied page size can ask for page before exceeding max returns true`() {
        //given
        val timesToAsk = 3
        val query = "szukam"
        val max = pageSize * timesToAsk + 2

        //when
        queryHolder.setQuery(query)
        queryHolder.setMax(query, max)
        (0 until timesToAsk).forEach { _ -> queryHolder.turnToNextPage() }

        //then
        assertEquals(queryHolder.canAskForAnotherOne(), true)
    }

    @Test
    fun `test when max is not multiplied page size can ask for page after exceeding max returns false`() {
        //given
        val timesToAsk = 3
        val query = "szukam"
        val max = pageSize * timesToAsk + 2

        //when
        queryHolder.setQuery(query)
        queryHolder.setMax(query, max)
        (0 until timesToAsk + 1).forEach { _ -> queryHolder.turnToNextPage() }

        //then
        assertEquals(queryHolder.canAskForAnotherOne(), false)
    }

    @Test
    fun `test provide query params after first setting the query`() {
        //given
        val query = "szukam"

        //when
        queryHolder.setQuery(query)

        //then
        assertEquals(queryHolder.provideQueryParams(), QueryParams(query, 0, pageSize))
    }

    @Test
    fun `test provide query params after turning the page some number of times`() {
        //given
        val query = "szukam"
        val numberOfPageTurn = Random.nextInt(1, 100)
        //when
        queryHolder.setQuery(query)
        (0 until numberOfPageTurn).forEach { _ -> queryHolder.turnToNextPage() }

        //then
        assertEquals(queryHolder.provideQueryParams(), QueryParams(query, numberOfPageTurn, pageSize))
    }

    @Test
    fun `test that after changing query query page is set back to 0`() {
        //given
        val query = "szukam"
        val secondTottalyDifferentQuery = "szukam2"

        //when
        queryHolder.setQuery(query)
        queryHolder.turnToNextPage()
        queryHolder.setQuery(secondTottalyDifferentQuery)

        //then
        assertEquals(queryHolder.provideQueryParams(), QueryParams(secondTottalyDifferentQuery, 0, pageSize))
    }

    @Test
    fun `test that setting the same query as previous does not have any consequences`() {
        //given
        val query = "szukam"

        //when
        queryHolder.setQuery(query)
        queryHolder.turnToNextPage()
        queryHolder.setQuery(query)

        //then
        assertEquals(queryHolder.provideQueryParams(), QueryParams(query, 1, pageSize))
    }

    @Test
    fun `test when changing query max is cleared`() {
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
        assertEquals(queryHolder.canAskForAnotherOne(), true)

    }
}