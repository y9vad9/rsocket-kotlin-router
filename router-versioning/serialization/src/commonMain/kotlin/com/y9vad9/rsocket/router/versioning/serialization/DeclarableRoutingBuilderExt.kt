@file:OptIn(ExperimentalInterceptorsApi::class, InternalRouterSerializationApi::class)

package com.y9vad9.rsocket.router.versioning.serialization

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.serialization.annotations.InternalRouterSerializationApi
import com.y9vad9.rsocket.router.serialization.decode
import com.y9vad9.rsocket.router.serialization.encode
import com.y9vad9.rsocket.router.serialization.preprocessor.SerializationProvider
import com.y9vad9.rsocket.router.versioning.PayloadStream
import com.y9vad9.rsocket.router.versioning.Version
import com.y9vad9.rsocket.router.versioning.builders.VersioningBuilder
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@JvmName("versionRequestResponse")
public inline fun <reified T : Any, reified R : Any> VersioningBuilder<Payload, Payload>.version(
    version: Version,
    crossinline block: suspend (T) -> R,
) {
    version(version) { payload ->
        val contentSerializer = SerializationProvider.getFromCoroutineContext()

        block(contentSerializer.decode(payload.data))
            .let { contentSerializer.encode(it) }
            .let { Payload(it) }
    }
}

@JvmName("versionFireAndForget")
public inline fun <reified T : Any> VersioningBuilder<Payload, Unit>.version(
    version: Version,
    crossinline block: suspend (T) -> Unit,
) {
    version(version) { payload ->
        val contentSerializer = SerializationProvider.getFromCoroutineContext()

        block(contentSerializer.decode(payload.data))
    }
}

@JvmName("versionRequestStream")
public inline fun <reified T : Any, reified R : Any> VersioningBuilder<Payload, Flow<Payload>>.version(
    version: Version,
    crossinline block: suspend (T) -> Flow<R>,
) {
    version(version) { payload ->
        val contentSerializer = SerializationProvider.getFromCoroutineContext()

        block(contentSerializer.decode(payload.data))
            .map { Payload(contentSerializer.encode(it)) }
    }
}

@JvmName("versionRequestChannel")
@OptIn(InternalRouterSerializationApi::class)
public inline fun <reified T : Any, reified R : Any> VersioningBuilder<PayloadStream, Flow<Payload>>.version(
    version: Version,
    crossinline block: suspend (initial: T, payloads: Flow<T>) -> Flow<R>,
) {
    version(version) { payload ->
        val contentSerializer = SerializationProvider.getFromCoroutineContext()

        val initial: T = contentSerializer.decode(payload.initPayload.data)
        val payloads: Flow<T> = payload.payloads.map { contentSerializer.decode(it.data) }

        block(initial, payloads)
            .map { Payload(contentSerializer.encode(it)) }
    }
}