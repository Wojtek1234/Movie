package mobi.wojtek.pagination.coroutine


import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import mobi.wojtek.pagination.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import pl.wojtek.testutils.CoroutinesTestExtension
import kotlin.test.assertEquals

/**
 *
 */
@InternalCoroutinesApi
class CoroutinPaginModelImpTest {

    @JvmField
    @RegisterExtension
    val coroutineScope = CoroutinesTestExtension()

    private lateinit var dataSource: CoroutineDataSource<String, DataFromSource>
    private lateinit var mapper: DataMapper<DataFromSource, DataResult, String>
    private lateinit var dataHolder: DataHolder<String, DataResult>
    private lateinit var queryDataHolder: QueryDataHolder<String>


    private lateinit var paginModel: CoroutinePaginModel<String, DataResult, DataFromSource>


    @BeforeEach
    fun setUp() {
        dataSource = mockk(relaxed = true)
        mapper = mockk(relaxed = true)
        dataHolder = mockk(relaxed = true)
        queryDataHolder = mockk(relaxed = true)

        paginModel = CoroutinPaginModelImp(dataSource, mapper, dataHolder, queryDataHolder)
    }

    @Test
    fun `test query data holder cannot ask for another one returns empty`() {
        runBlockingTest {
            //given
            coEvery { queryDataHolder.canAskForAnotherOne() } returns false

            //when

            assertEquals(paginModel.askForMore(), null)
        }

    }

    @Test
    fun `test setting query triggers method of queryDataHolder`() {
        runBlockingTest {
            //given
            val query = "tralala"

            //when
            paginModel.setQuery(query)

            //then
            coVerify { queryDataHolder.setQuery(query) }
        }
    }

    @Test
    fun `test all is good scenario`() {
        runBlockingTest {
            //given
            val query = "tralala"
            val dataFromSource = DataFromSource("data from hipotetical api")
            val dataResult = DataResult("data after mapping")
            val queryParams = QueryParams(query, 0, 20)

            val mappedData = MappedData(query, listOf(dataResult))
            coEvery { dataSource.askForData(queryParams) } returns dataFromSource
            coEvery { mapper.map(dataFromSource, queryParams) } returns mappedData
            coEvery { dataHolder.provideData(query, mappedData.list) } returns mappedData.list
            coEvery { queryDataHolder.canAskForAnotherOne() } returns true

            coEvery { queryDataHolder.provideQueryParams() } returns queryParams

            //when then

            paginModel.setQuery(query)
            val askForMore = paginModel.askForMore()
            assertEquals(askForMore, mappedData.list)

        }

    }

    @Test
    fun `test proper triggering of objects in good scenario`() {
        runBlockingTest {
            //given
            val query = "tralala"
            val dataFromSource = DataFromSource("data from hipotetical api")
            val dataResult = DataResult("data after mapping")
            val queryParams = QueryParams(query, 0, 20)
            val mappedData = MappedData(query, listOf(dataResult), 123)
            coEvery { dataSource.askForData(queryParams) } returns dataFromSource
            coEvery { mapper.map(dataFromSource, queryParams) } returns mappedData
            coEvery { dataHolder.provideData(query, mappedData.list) } returns mappedData.list
            coEvery { queryDataHolder.canAskForAnotherOne() } returns true

            coEvery { queryDataHolder.provideQueryParams() } returns queryParams

            //when
            paginModel.setQuery(query)
            val tested = paginModel.askForMore()

            //then
            coVerifySequence {
                queryDataHolder.setQuery(query)
                queryDataHolder.canAskForAnotherOne()
                queryDataHolder.provideQueryParams()
                dataSource.askForData(queryParams)
                queryDataHolder.provideQueryParams()
                mapper.map(dataFromSource, queryParams)
                queryDataHolder.turnToNextPage()
                queryDataHolder.setMax(query, mappedData.max)
                dataHolder.provideData(query, mappedData.list)

            }
        }
    }

    @Test
    fun `test when exception no turning the page on`() {
        runBlockingTest {
            //given
            val query = "tralala"
            val queryParams = QueryParams(query, 0, 20)

            coEvery { dataSource.askForData(queryParams) } throws NullPointerException()

            coEvery { queryDataHolder.canAskForAnotherOne() } returns true

            coEvery { queryDataHolder.provideQueryParams() } returns queryParams

            //when
            paginModel.setQuery(query)
            try {
                paginModel.askForMore()
            } catch (ex: java.lang.NullPointerException) {

            } finally {
                //then

                coVerifySequence {
                    queryDataHolder.setQuery(query)

                    queryDataHolder.canAskForAnotherOne()

                    queryDataHolder.provideQueryParams()

                    dataSource.askForData(queryParams)
                }

            }
        }
    }

    @Test
    fun `test loading stream`() {
        runBlockingTest {
            //given
            val query = "tralala"
            val dataFromSource = DataFromSource("data from hipotetical api")
            val dataResult = DataResult("data after mapping")

            val queryParams = QueryParams(query, 0, 20)
            val mappedData = MappedData(query, listOf(dataResult))

            coEvery { dataSource.askForData(queryParams) } returns dataFromSource
            coEvery { mapper.map(dataFromSource, queryParams) } returns mappedData
            coEvery { dataHolder.provideData(query, mappedData.list) } returns mappedData.list
            coEvery { queryDataHolder.canAskForAnotherOne() } returns true

            coEvery { queryDataHolder.provideQueryParams() } returns queryParams

            val values = mutableListOf<Boolean>()
            //when
            paginModel.setQuery(query)
            val job = launch {
                paginModel.loadingState().collect {
                    values.add(it)
                }
            }
            //then
            assertEquals(values.size, 1)
            assertEquals(values[0], false)
            //when
            paginModel.askForMore()

            //then
            assertEquals(values.size, 3)
            assertEquals(values[1], true)
            assertEquals(values[2], false)


            job.cancel()
        }
    }

    @Test
    fun `test on api exception throw exception`() {
        runBlockingTest {
            //given
            val query = "tralala"
            val dataFromSource = DataFromSource("data from hipotetical api")

            val queryParams = QueryParams(query, 0, 20)
            coEvery {
                dataSource.askForData(queryParams)
            } throws java.lang.NullPointerException()

            coEvery { queryDataHolder.canAskForAnotherOne() } returns true

            coEvery { queryDataHolder.provideQueryParams() } returns queryParams

            //when then
            paginModel.setQuery(query)
            Assertions.assertThrows(java.lang.NullPointerException::class.java) {
                runBlocking {
                    paginModel.askForMore()
                }
            }

        }

    }

    @Test
    fun `test that after exception loading stream send false`() {
        runBlockingTest {

            //given
            val query = "tralala"


            val queryParams = QueryParams(query, 0, 20)
            coEvery {
                dataSource.askForData(queryParams)
            } throws java.lang.NullPointerException()

            coEvery { queryDataHolder.canAskForAnotherOne() } returns true

            coEvery { queryDataHolder.provideQueryParams() } returns queryParams

            val values = mutableListOf<Boolean>()

            //when then
            paginModel.setQuery(query)
            val job = launch {
                paginModel.loadingState().collect {
                    values.add(it)
                }
            }
            //then
            assertEquals(values.size, 1)
            assertEquals(values[0], false)
            //when
            Assertions.assertThrows(java.lang.NullPointerException::class.java) {
                runBlocking {
                    paginModel.askForMore()
                }
            }
            //then
            assertEquals(values.size, 3)
            assertEquals(values[1], true)
            assertEquals(values[2], false)


            job.cancel()
        }
    }

}



