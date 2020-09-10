package mobi.wojtek.pagination.coroutine


import io.kotlintest.TestCase
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import mobi.wojtek.pagination.*

/**
 *
 */
@InternalCoroutinesApi
class CoroutinPaginModelImpTest : StringSpec() {
    private lateinit var dataSource: CoroutineDataSource<String, DataFromSource>
    private lateinit var mapper: DataMapper<DataFromSource, DataResult, String>
    private lateinit var dataHolder: DataHolder<String, DataResult>
    private lateinit var queryDataHolder: QueryDataHolder<String>


    private lateinit var paginModel: CoroutinePaginModel<String, DataResult, DataFromSource>


    override fun beforeTest(testCase: TestCase) {
        dataSource = mockk(relaxed = true)
        mapper = mockk(relaxed = true)
        dataHolder = mockk(relaxed = true)
        queryDataHolder = mockk(relaxed = true)

        paginModel = CoroutinPaginModelImp(dataSource, mapper, dataHolder, queryDataHolder)
    }

    init {
        "test query data holder cannot ask for another one returns empty"{
            runBlockingTest {
                //given
                coEvery { queryDataHolder.canAskForAnotherOne() } returns false

                //when

                paginModel.askForMore() shouldBe null
            }

        }

        "test setting query triggers method of queryDataHolder"{
            //given
            val query = "tralala"

            //when
            paginModel.setQuery(query)

            //then
            coVerify { queryDataHolder.setQuery(query) }
        }

        "test all is good scenario"{
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
                askForMore shouldBe mappedData.list

            }

        }

        "test proper triggering of objects in good scenario"{
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

        "test when exception no turning the page on"{
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

        "test loading stream"{
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
                values shouldHaveSize 1
                values[0] shouldBe false
                //when
                paginModel.askForMore()

                //then
                values shouldHaveSize 3
                values[1] shouldBe true
                values[2] shouldBe false


                job.cancel()
            }
        }

        "test on api exception throw exception"{
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
                shouldThrow<java.lang.NullPointerException> {
                    paginModel.askForMore()
                }

            }

        }
        "test that after exception loading stream send false"{
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
                values shouldHaveSize 1
                values[0] shouldBe false
                //when
                shouldThrow<java.lang.NullPointerException> {
                    paginModel.askForMore()
                }
                //then
                values shouldHaveSize 3
                values[1] shouldBe true
                values[2] shouldBe false


                job.cancel()
            }
        }

    }



}