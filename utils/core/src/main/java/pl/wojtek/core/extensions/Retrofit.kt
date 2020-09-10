package pl.wojtek.core.extensions

import org.koin.core.scope.Scope
import retrofit2.Retrofit

/**
 *
 */

inline fun <reified T> Scope.getApi(): T = get<Retrofit>().create(T::class.java)