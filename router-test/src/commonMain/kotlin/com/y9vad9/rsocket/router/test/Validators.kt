package com.y9vad9.rsocket.router.test

import com.y9vad9.rsocket.router.Route
import com.y9vad9.rsocket.router.Router
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor
import io.ktor.util.reflect.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

@OptIn(ExperimentalRouterApi::class)
public inline fun <reified T : RouteInterceptor<*, *>> Route.assertHasInterceptor(): Unit =
    assertHasInterceptor(T::class)

@ExperimentalRouterApi
public fun <T : RouteInterceptor<*, *>> Route.assertHasInterceptor(ofClass: KClass<T>) {
    interceptors.firstOrNull { it.instanceOf(ofClass) }
        ?: throw AssertionError("Required interceptor `${ofClass.simpleName}` is not found for `$path` route.")
}

@ExperimentalRouterApi
public inline fun <reified T : Preprocessor<*, *>> Route.assertHasPreprocessor(): Unit =
    assertHasPreprocessor(T::class)

@ExperimentalRouterApi
public fun <T : Preprocessor<*, *>> Route.assertHasPreprocessor(ofClass: KClass<T>) {
    preprocessors.firstOrNull { it.instanceOf(ofClass) }
        ?: throw AssertionError(
            "Required preprocessor `${ofClass.simpleName}` is not found for `$path` route."
        )
}

public suspend fun Route.fireAndForgetOrAssert(rSocket: RSocket, payload: Payload): Unit = try {
    fireAndForgetOrThrow(rSocket, payload)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute fire-and-forget method on `$path` route.", e)
}

public suspend fun Route.requestResponseOrAssert(rSocket: RSocket, payload: Payload): Payload = try {
    requestResponseOrThrow(rSocket, payload)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute request-response method on `$path` route.", e)
}

public suspend fun Route.requestStreamOrAssert(rSocket: RSocket, payload: Payload): Flow<Payload> = try {
    requestStreamOrThrow(rSocket, payload)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute request-stream method on `$path` route.", e)
}

public suspend fun Route.requestChannelOrAssert(
    rSocket: RSocket,
    initPayload: Payload,
    payloads: Flow<Payload>,
): Flow<Payload> = try {
    requestChannelOrThrow(rSocket, initPayload, payloads)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute request-channel method on `$path` route.", e)
}

public fun Router.routeAtOrAssert(path: String): Route = routeAt(path)
    ?: throw AssertionError("Route at `$path` path is not found.")