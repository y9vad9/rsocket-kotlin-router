package com.y9vad9.rsocket.router.versioning

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.builders.RouterBuilder
import com.y9vad9.rsocket.router.versioning.preprocessor.RequestVersionProvider
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.payload.Payload
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalInterceptorsApi::class)
public fun RouterBuilder.versioning(
    block: (metadata: ByteReadPacket?, coroutineContext: CoroutineContext) -> Version,
) {
    preprocessors {
        forCoroutineContext(object : RequestVersionProvider() {
            override fun version(payload: Payload, coroutineContext: CoroutineContext): Version {
                return block(payload.metadata, coroutineContext)
            }
        })
    }
}