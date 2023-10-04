package com.y9vad9.rsocket.router.serialization.context

import com.y9vad9.rsocket.router.serialization.ContentSerializer
import kotlin.coroutines.CoroutineContext

internal data class SerializationContext(
    val contentSerializer: ContentSerializer,
) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key

    companion object Key : CoroutineContext.Key<SerializationContext>
}