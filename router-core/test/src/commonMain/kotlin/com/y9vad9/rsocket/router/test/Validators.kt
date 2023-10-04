package com.y9vad9.rsocket.router.test

import com.y9vad9.rsocket.router.Route
import com.y9vad9.rsocket.router.Router
import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor
import io.ktor.util.reflect.*
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

@ExperimentalInterceptorsApi
public inline fun <reified T : RouteInterceptor> Route.assertHasInterceptor(): Unit =
    assertHasInterceptor(T::class)

@ExperimentalInterceptorsApi
public fun <T : RouteInterceptor> Route.assertHasInterceptor(ofClass: KClass<T>) {
    interceptors.firstOrNull { it.instanceOf(ofClass) }
        ?: throw AssertionError("Required interceptor `${ofClass.simpleName}` is not found for `$path` route.")
}

@ExperimentalInterceptorsApi
public inline fun <reified T : Preprocessor> Route.assertHasPreprocessor(): Unit =
    assertHasPreprocessor(T::class)

@ExperimentalInterceptorsApi
public fun <T : Preprocessor> Route.assertHasPreprocessor(ofClass: KClass<T>) {
    preprocessors.firstOrNull { it.instanceOf(ofClass) }
        ?: throw AssertionError(
            "Required preprocessor `${ofClass.simpleName}` is not found for `$path` route."
        )
}

public suspend fun Route.fireAndForgetOrAssert(payload: Payload): Unit = try {
    fireAndForget(payload)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute fire-and-forget method on `$path` route.", e)
}

public suspend fun Route.requestResponseOrAssert(payload: Payload): Payload = try {
    requestResponse(payload)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute request-response method on `$path` route.", e)
}

public suspend fun Route.requestStreamOrAssert(payload: Payload): Flow<Payload> = try {
    requestStream(payload)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute request-stream method on `$path` route.", e)
}

public suspend fun Route.requestChannelOrAssert(
    initPayload: Payload,
    payloads: Flow<Payload>,
): Flow<Payload> = try {
    requestChannel(initPayload, payloads)
} catch (e: Throwable) {
    throw AssertionError("Failed to execute request-channel method on `$path` route.", e)
}

public fun Router.routeAtOrAssert(path: String): Route = routeAt(path)
    ?: throw AssertionError("Route at `$path` path is not found.")

public fun Router.assertHasRoute(path: String) {
    routeAtOrAssert(path)
}