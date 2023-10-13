package com.y9vad9.rsocket.router.serialization.preprocessor

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.builders.RouterBuilder
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.serialization.ContentSerializer
import com.y9vad9.rsocket.router.serialization.annotations.ExperimentalRouterSerializationApi
import com.y9vad9.rsocket.router.serialization.annotations.InternalRouterSerializationApi
import com.y9vad9.rsocket.router.serialization.context.SerializationContext
import io.rsocket.kotlin.payload.Payload
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@ExperimentalInterceptorsApi
public abstract class SerializationProvider : Preprocessor.CoroutineContext {
    public companion object {
        /**
         * Retrieves the [ContentSerializer] from the coroutine context.
         *
         * **API note**:
         * You shouldn't call this function yourself unless you use it to define your own extensions
         * that should be dependent on it.
         *
         * **Failure note**:
         * 1) if you didn't call it yourself and, probably you need to register `SerializationProvider` by
         * putting it in the preprocessors or by using `serialization` function in `RoutingBuilder`.
         * 2) if function wasn't called by you intentionally and `SerializationProvider` is already
         * registered, but inside `test` artifact you should provide content serializer to context using `asContextElement`.
         *
         * **Testing note**:
         * 1) Before testing specific route take in count that `Route` does not run preprocessors, you should
         * do it manually using [com.y9vad9.rsocket.router.test.preprocess] or [com.y9vad9.rsocket.router.intercepts]
         * functions.
         *
         * @return The [ContentSerializer] obtained from the coroutine context.
         * @throws IllegalStateException If the [ContentSerializer] was not provided or the method was called from
         * an illegal context.
         */
        @InternalRouterSerializationApi
        public suspend fun getFromCoroutineContext(): ContentSerializer {
            return coroutineContext[SerializationContext]?.contentSerializer
                ?: error("ContentSerializer wasn't provided or call happened from illegal context")
        }

        @ExperimentalRouterSerializationApi
        public suspend fun asContextElement(serializer: ContentSerializer): CoroutineContext =
            coroutineContext + SerializationContext(serializer)
    }

    public abstract fun provide(coroutineContext: CoroutineContext, payload: Payload): ContentSerializer

    final override fun intercept(coroutineContext: CoroutineContext, input: Payload): CoroutineContext {
        return coroutineContext + SerializationContext(provide(coroutineContext, input))
    }
}

public fun RouterBuilder.serialization(block: () -> ContentSerializer) {
    serialization { _, _ -> block() }
}

@OptIn(ExperimentalInterceptorsApi::class)
public fun RouterBuilder.serialization(block: (CoroutineContext, Payload) -> ContentSerializer) {
    preprocessors {
        forCoroutineContext(
            object : SerializationProvider() {
                override fun provide(
                    coroutineContext: CoroutineContext,
                    payload: Payload,
                ): ContentSerializer {
                    return block(coroutineContext, payload)
                }
            }
        )
    }
}