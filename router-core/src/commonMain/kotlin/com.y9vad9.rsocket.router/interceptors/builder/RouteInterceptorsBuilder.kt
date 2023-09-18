package com.y9vad9.rsocket.router.interceptors.builder

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor

@ExperimentalInterceptorsApi
public class RouteInterceptorsBuilder internal constructor() {
    private val interceptors = mutableListOf<RouteInterceptor>()

    public fun forCoroutineContext(interceptor: RouteInterceptor.CoroutineContext) {
        interceptors += interceptor
    }

    public fun forModification(interceptor: RouteInterceptor.Modifier) {
        interceptors += interceptor
    }

    internal fun build(): List<RouteInterceptor> = interceptors.toList()
}