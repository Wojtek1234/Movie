package mobi.wojtek.pagination



/**
 *
 */




data class MappedData<Q, R>(val query: Q, val list: List<R>, val max: Int = 0)
interface DataMapper<A, R, Q> {
    fun map(a: A, q: QueryParams<Q>): MappedData<Q, R>
}

