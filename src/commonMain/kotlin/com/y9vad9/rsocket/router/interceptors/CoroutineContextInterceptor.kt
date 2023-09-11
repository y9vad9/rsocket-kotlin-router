package com.y9vad9.rsocket.router.interceptors

import com.y9vad9.rsocket.router.ExperimentalRouterApi
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.core.Interceptor
import io.rsocket.kotlin.payload.Payload
import kotlin.coroutines.CoroutineContext


public data class InterceptedRequest(
    val payload: Payload,
    val coroutineContext: CoroutineContext,
)

/**
 * Interceptor to propagate values through coroutine context.
 *
 * This abstract class extends the [Interceptor] class with the type parameter [InterceptedRequest]. It provides
 * an implementation for the [intercept] function and requires subclasses to implement the [coroutineContext] function.
 *
 * The [coroutineContext] function is an abstract function that must be implemented by subclasses. It takes the
 * [metadata] of type [ByteReadPacket] and the existing [coroutineContext] of type [CoroutineContext]. It returns
 * a new [CoroutineContext] that incorporates the [metadata] and the existing [coroutineContext].
 */
@ExperimentalRouterApi
public abstract class CoroutineContextInterceptor : Interceptor<InterceptedRequest> {
    final override fun intercept(input: InterceptedRequest): InterceptedRequest = with(input) {
        return copy(
            payload = payload.copy(),
            coroutineContext = coroutineContext(
                payload,
                coroutineContext
            ),
        )
    }

    public abstract fun coroutineContext(
        payload: Payload,
        coroutineContext: CoroutineContext,
    ): CoroutineContext
}