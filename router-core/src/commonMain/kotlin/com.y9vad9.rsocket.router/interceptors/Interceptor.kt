package com.y9vad9.rsocket.router.interceptors

import io.ktor.utils.io.core.*
import io.rsocket.kotlin.payload.Payload
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi

/**
 * An interceptor for the RSocket library used by Router.
 *
 * @param <T> The type of the request.
 * @param <R> The return type of the interceptor.
 */
@ExperimentalRouterApi
public sealed interface Interceptor<T, R>

/**
 * This interface represents a preprocessor, which is an interceptor for intercepting requests before the route feature.
 *
 * @param T the input type of the preprocessor
 * @param R the output type of the preprocessor
 */
@ExperimentalRouterApi
public sealed interface Preprocessor<T, R> : Interceptor<T, R> {
    /**
     * A coroutine context, which is responsible for preprocessing payloads
     * and intercepting coroutine execution.
     *
     * **Incoming payload should be copied itself if needed**. By default,
     * it's not copied after / before Preprocessor is called.
     */
    public fun interface CoroutineContext : Preprocessor<Payload, CoroutineContext> {
        public fun intercept(coroutineContext: kotlin.coroutines.CoroutineContext, input: Payload): kotlin.coroutines.CoroutineContext
    }

    /**
     * This interface represents a modifier that can be used to preprocess payloads.
     * @see Preprocessor
     */
    public fun interface Modifier : Preprocessor<Payload, Payload> {
        public fun intercept(input: Payload): Payload
    }
}


/**
 * Interceptr that works after route feature.
 */
@ExperimentalRouterApi
public sealed interface RouteInterceptor<T, R> : Interceptor<T, R> {

    /**
     * The CoroutineContext interface is used to propagate values to request execution.
     * **Incoming payload should be copied itself if needed**. By default,
     * it's not copied after / before Preprocessor is called.
     */
    public fun interface CoroutineContext : RouteInterceptor<RouteAwarePayload, kotlin.coroutines.CoroutineContext> {
        public fun intercept(coroutineContext: kotlin.coroutines.CoroutineContext, input: Payload): kotlin.coroutines.CoroutineContext
    }


    /**
     * The `Modifier` interface is used to modify an incoming payload with a route.
     * It is defined as a route interceptor and extends the `RouteInterceptor` interface.
     *
     * @param <RouteAwarePayload> The type of payload that contains information about the route.
     * @param <Payload> The type of payload to be modified.
     */
    public fun interface Modifier : RouteInterceptor<RouteAwarePayload, Payload> {
        public fun intercept(input: Payload): Payload
    }
}

/**
 * Represents a payload that is aware of the route it belongs to.
 *
 * @property route The route associated with the payload.
 * @property data The data packet of the payload.
 * @property metadata The optional metadata packet of the payload.
 */
public data class RouteAwarePayload(
    public val route: String,
    public val data: ByteReadPacket,
    public val metadata: ByteReadPacket?,
)