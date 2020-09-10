package mobi.wojtek.pagination

/**
 *
 */


internal interface DataHolder<Q, R> {
    fun provideData(q: Q, list: List<R>): List<R>
    fun clearData() {}
}


internal class DataHolderImp<Q, R> : DataHolder<Q, R> {
    private var data: Pair<Q, List<R>>? = null

    override fun provideData(q: Q, list: List<R>): List<R> {
        data = when {
            data == null -> q to list
            data!!.first != q -> q to list
            else -> q to data!!.second + list
        }

        return data!!.second
    }

    override fun clearData() {
        data = null
    }
}