package mobi.wojtek.pagination

import mobi.wojtek.pagination.coroutine.CoroutinePaginModelFactory
import mobi.wojtek.pagination.coroutine.CoroutinePaginModelFactoryImp
import org.koin.dsl.module

/**
 *
 */


val coroutinePaginationModule = module {
    factory<CoroutinePaginModelFactory> { CoroutinePaginModelFactoryImp() }
}