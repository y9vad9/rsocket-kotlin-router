package com.y9vad9.rsocket.router

import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.core.Interceptor
import com.y9vad9.rsocket.router.interceptors.InterceptedRequest

public class InterceptorsBuilder internal constructor() {

    private val coroutineContextInterceptors = mutableListOf<Interceptor<InterceptedRequest>>()

    public fun forCoroutineContext(interceptor: Interceptor<InterceptedRequest>) {
        coroutineContextInterceptors += interceptor
    }

    @OptIn(ExperimentalMetadataApi::class)
    internal fun build(): Interceptors = Interceptors(coroutineContextInterceptors)
}

internal data class Interceptors(
    @OptIn(ExperimentalMetadataApi::class)
    val coroutineContextInterceptors: List<Interceptor<InterceptedRequest>> = emptyList(),
) {

    fun wrapRequest(
        request: InterceptedRequest,
    ): InterceptedRequest = coroutineContextInterceptors.fold(request) { acc, interceptor ->
        interceptor.intercept(acc)
    }
}
