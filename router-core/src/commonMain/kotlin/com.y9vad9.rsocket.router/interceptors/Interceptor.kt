package com.y9vad9.rsocket.router.interceptors

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.payload.Payload
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi

/**
 * An interceptor for the RSocket library used by Router.
 *
 * @param <T> The type of the request.
 * @param <R> The return type of the interceptor.
 */
@ExperimentalInterceptorsApi
public sealed interface Interceptor

/**
 * This interface represents a preprocessor, which is an interceptor for intercepting requests before the route feature.
 *
 * @param T the input type of the preprocessor
 * @param R the output type of the preprocessor
 */
@ExperimentalInterceptorsApi
public sealed interface Preprocessor : Interceptor {
    /**
     * A coroutine context, which is responsible for preprocessing payloads
     * and intercepting coroutine execution.
     *
     * **Incoming payload should be copied itself if needed**. By default,
     * it's not copied after / before Preprocessor is called.
     */
    public fun interface CoroutineContext : Preprocessor {
        public fun intercept(coroutineContext: kotlin.coroutines.CoroutineContext, input: Payload): kotlin.coroutines.CoroutineContext
    }

    /**
     * This interface represents a modifier that can be used to preprocess payloads.
     * @see Preprocessor
     */
    public fun interface Modifier : Preprocessor {
        public fun intercept(input: Payload): Payload
    }
}


/**
 * Interceptr that works after route feature.
 */
@ExperimentalInterceptorsApi
public sealed interface RouteInterceptor : Interceptor {

    /**
     * The CoroutineContext interface is used to propagate values to request execution.
     * **Incoming payload should be copied itself if needed**. By default,
     * it's not copied after / before Preprocessor is called.
     */
    public fun interface CoroutineContext : RouteInterceptor {
        public fun intercept(
            route: String,
            coroutineContext: kotlin.coroutines.CoroutineContext,
            input: Payload,
        ): kotlin.coroutines.CoroutineContext
    }


    /**
     * The `Modifier` interface is used to modify an incoming payload with a route.
     * It is defined as a route interceptor and extends the `RouteInterceptor` interface.
     *
     * @param <RouteAwarePayload> The type of payload that contains information about the route.
     * @param <Payload> The type of payload to be modified.
     */
    public fun interface Modifier : RouteInterceptor {
        public fun intercept(route: String, input: Payload): Payload
    }
}