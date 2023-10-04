@file:OptIn(
    ExperimentalInterceptorsApi::class, InternalRouterSerializationApi::class,
    InternalRouterSerializationApi::class,
)

package com.y9vad9.rsocket.router.serialization.test

import com.y9vad9.rsocket.router.Route
import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.serialization.annotations.InternalRouterSerializationApi
import com.y9vad9.rsocket.router.serialization.decode
import com.y9vad9.rsocket.router.serialization.encode
import com.y9vad9.rsocket.router.serialization.preprocessor.SerializationProvider
import com.y9vad9.rsocket.router.test.fireAndForgetOrAssert
import com.y9vad9.rsocket.router.test.requestChannelOrAssert
import com.y9vad9.rsocket.router.test.requestResponseOrAssert
import com.y9vad9.rsocket.router.test.requestStreamOrAssert
import io.ktor.server.routing.*
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public suspend inline fun <reified T : Any, reified R : Any> Route.requestResponseOrAssert(
    data: T,
    metadata: ByteReadPacket? = null,
): R {
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    return requestResponseOrAssert(
        Payload(
            data = contentSerializer.encode(data),
            metadata = metadata,
        )
    ).let { contentSerializer.decode(it.data) }
}

public suspend inline fun <reified T : Any> Route.fireAndForgetOrAssert(
    data: T,
    metadata: ByteReadPacket? = null,
) {
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    return fireAndForgetOrAssert(
        Payload(
            data = contentSerializer.encode(data),
            metadata = metadata,
        )
    )
}

public suspend inline fun <reified T : Any, reified R : Any> Route.requestStreamOrAssert(
    data: T,
    metadata: ByteReadPacket? = null,
): Flow<R> {
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    return requestStreamOrAssert(
        Payload(
            data = contentSerializer.encode(data),
            metadata = metadata,
        )
    ).let { flow ->
        flow.map { contentSerializer.decode(it.data) }
    }
}

public suspend inline fun <reified T : Any, reified R : Any> Route.requestChannelOrAssert(
    initial: T,
    payloads: Flow<T>,
    metadata: ByteReadPacket? = null,
): Flow<R> {
    val contentSerializer = SerializationProvider.getFromCoroutineContext()

    return requestChannelOrAssert(
        initPayload = Payload(
            data = contentSerializer.encode(initial),
            metadata = metadata,
        ),
        payloads = payloads.map { Payload(contentSerializer.encode(it)) },
    ).let { flow ->
        flow.map { contentSerializer.decode(it.data) }
    }
}