package mobi.wojtek.pagination

/**
 *
 */

data class QueryParams<Q>(val query: Q?, val page: Int = 0, val pageSize: Int = 20)


internal interface QueryDataHolder<Q> {
    fun setQuery(q: Q)
    fun setMax(q: Q, max: Int)
    fun canAskForAnotherOne(): Boolean
    fun provideQueryParams(): QueryParams<Q>
    fun turnToNextPage()
    fun clearQuery()
}


internal class QueryDataHolderImp<Q>(val pageSize: Int = 20) : QueryDataHolder<Q> {

    private var queryParams: QueryParams<Q> = QueryParams(null, pageSize = pageSize)
    private var maximum: Pair<Q, Int>? = null

    override fun setQuery(q: Q) {
        if (q != queryParams.query) {
            queryParams = QueryParams(q, 0, pageSize)
            maximum = null
        }
    }

    override fun setMax(q: Q, max: Int) {
        maximum = q to max
    }

    override fun canAskForAnotherOne(): Boolean = when {
        queryParams.query == null -> false
        maximum == null -> true
        queryParams.page * queryParams.pageSize < maximum!!.second -> true
        queryParams.page * queryParams.pageSize >= maximum!!.second -> false
        else -> true
    }


    override fun provideQueryParams(): QueryParams<Q> = queryParams

    override fun turnToNextPage() {
        queryParams = queryParams.copy(page = queryParams.page + 1)
    }

    override fun clearQuery() {
        queryParams = QueryParams(null, 0, pageSize)
        maximum = null
    }
}