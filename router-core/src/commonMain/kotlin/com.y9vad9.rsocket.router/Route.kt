@file:Suppress("NAME_SHADOWING")

package com.y9vad9.rsocket.router

import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalRouterApi::class)
public data class Route internal constructor(
    val path: String,
    @property:ExperimentalRouterApi
    internal val requests: Requests,
    val preprocessors: List<Preprocessor<*, *>>,
    val interceptors: List<RouteInterceptor<*, *>>,
) {
    public suspend fun fireAndForgetOrThrow(rSocket: RSocket, payload: Payload) {
        processPayload(payload) { payload ->
            requests.fireAndForget?.invoke(rSocket, payload)
                ?: throwInvalidRequestOnRoute("fireAndForget")
        }
    }

    public suspend fun requestResponseOrThrow(rSocket: RSocket, payload: Payload): Payload {
        return processPayload(payload) { payload ->
            requests.requestResponse?.invoke(rSocket, payload)
                ?: throwInvalidRequestOnRoute("requestResponse")
        }
    }

    public suspend fun requestStreamOrThrow(rSocket: RSocket, payload: Payload): Flow<Payload> {
        return processPayload(payload) { payload ->
            requests.requestStream?.invoke(rSocket, payload)
                ?: throwInvalidRequestOnRoute("requestStream")
        }
    }

    public suspend fun requestChannelOrThrow(
        rSocket: RSocket,
        initPayload: Payload,
        payloads: Flow<Payload>,
    ): Flow<Payload> = processPayload(initPayload) { initialPayload ->
        requests.requestChannel?.invoke(rSocket, initialPayload, payloads)
            ?: throwInvalidRequestOnRoute("requestChannel")
    }

    private suspend inline fun <R> processPayload(payload: Payload, crossinline block: suspend (Payload) -> R): R {
        var coroutineContext: CoroutineContext = currentCoroutineContext()

        val payload =
            interceptors.fold(payload) { acc, interceptor ->
                when (interceptor) {
                    is RouteInterceptor.Modifier -> interceptor.intercept(acc)
                    is RouteInterceptor.CoroutineContext -> {
                        coroutineContext = interceptor.intercept(coroutineContext, acc)
                        acc
                    }
                }
            }

        return withContext(coroutineContext) {
            block(payload)
        }
    }

    internal data class Requests(
        val fireAndForget: (suspend RSocket.(payload: Payload) -> Unit)? = null,
        val requestResponse: (suspend RSocket.(payload: Payload) -> Payload)? = null,
        val requestStream: (suspend RSocket.(payload: Payload) -> Flow<Payload>)? = null,
        val requestChannel: (suspend RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>)? = null,
    )
}

private fun Route.throwInvalidRequestOnRoute(requestType: String): Nothing =
    throw RSocketError.Invalid("No `$requestType` is registered for `$path` route.")