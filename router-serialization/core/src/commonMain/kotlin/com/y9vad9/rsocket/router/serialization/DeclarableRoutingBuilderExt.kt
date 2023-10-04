@file:OptIn(
    ExperimentalInterceptorsApi::class,
    InternalRouterSerializationApi::class,
)

package com.y9vad9.rsocket.router.serialization

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.builders.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.serialization.annotations.InternalRouterSerializationApi
import com.y9vad9.rsocket.router.serialization.preprocessor.SerializationProvider
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.reflect.typeOf

/**
 * Executes a request-response operation with the given payload.
 *
 * @param T the type of the input payload.
 * @param R the type of the output payload.
 * @param block the suspend lambda that performs the request-response operation.
 */
public inline fun <reified T : Any, reified R : Any> DeclarableRoutingBuilder.requestResponse(
    crossinline block: suspend (T) -> R,
): Unit = requestResponse { payload ->
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    block(contentSerializer.decode(typeOf<T>(), payload.data))
        .let { result -> contentSerializer.encode(typeOf<R>(), result) }
        .let { data -> Payload(data = data) }
}

/**
 * Executes a streaming request with the given payload.
 *
 * @param T the type of the payload to be decoded.
 * @param R the type of the result to be encoded.
 * @param block the suspend lambda function that takes the decoded payload and returns a Flow of results.
 *
 * @return Unit
 */
public inline fun <reified T : Any, reified R : Any> DeclarableRoutingBuilder.requestStream(
    crossinline block: suspend (T) -> Flow<R>,
): Unit = requestStream { payload ->
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    block(contentSerializer.decode(typeOf<T>(), payload.data))
        .map { result -> Payload(data = contentSerializer.encode(typeOf<R>(), result)) }
}

/**
 * Executes a given suspend block without expecting any return value.
 *
 * @param block The suspend block to be executed.
 * @param T The type of the payload to be passed to the block.
 */
public inline fun <reified T : Any> DeclarableRoutingBuilder.fireAndForget(
    crossinline block: suspend (T) -> Unit,
): Unit = fireAndForget { payload ->
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    block(contentSerializer.decode<T>(payload.data))
}

/**
 * Sends a request channel to the router and provides a response channel.
 * This method is used to send a series of request elements (`T`) to the router,
 * receive a series of response elements (`R`) from the router, and complete the channel.
 *
 * @param T the type of the initial request element and subsequent request elements
 * @param R the type of the response elements
 * @param block the suspending lambda function that processes the incoming request elements and returns the response elements
 */
public inline fun <reified T : Any, reified R : Any> DeclarableRoutingBuilder.requestChannel(
    crossinline block: suspend (initial: T, Flow<T>) -> Flow<R>,
): Unit = requestChannel { initial: Payload, payloads: Flow<Payload> ->
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    val init = contentSerializer.decode<T>(initial.data)
    val mappedPayloads: Flow<T> = payloads.map { contentSerializer.decode(it.data) }

    block(init, mappedPayloads)
        .map { result -> Payload(data = contentSerializer.encode<R>(result)) }
}


public inline fun <reified T : Any, reified R : Any> DeclarableRoutingBuilder.requestResponse(
    path: String,
    crossinline block: suspend (T) -> R,
): Unit = route(path) {
    requestResponse(block)
}

public inline fun <reified T : Any, reified R : Any> DeclarableRoutingBuilder.requestStream(
    path: String,
    crossinline block: suspend (T) -> Flow<R>,
): Unit = route(path) {
    requestStream(block)
}

public inline fun <reified T : Any> DeclarableRoutingBuilder.fireAndForget(
    path: String,
    crossinline block: suspend (T) -> Unit,
): Unit = route(path) {
    fireAndForget(block)
}

public inline fun <reified T : Any, reified R : Any> DeclarableRoutingBuilder.requestChannel(
    path: String,
    crossinline block: suspend (initial: T, Flow<T>) -> Flow<R>,
): Unit = route(path) {
    requestChannel(block)
}