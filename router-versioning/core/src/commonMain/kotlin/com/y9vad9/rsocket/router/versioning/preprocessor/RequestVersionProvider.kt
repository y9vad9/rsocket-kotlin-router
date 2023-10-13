package com.y9vad9.rsocket.router.versioning.preprocessor

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.versioning.Version
import com.y9vad9.rsocket.router.versioning.annotations.ExperimentalVersioningApi
import com.y9vad9.rsocket.router.versioning.annotations.InternalVersioningApi
import io.rsocket.kotlin.payload.Payload
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@ExperimentalInterceptorsApi
public abstract class RequestVersionProvider : Preprocessor.CoroutineContext {
    internal data class VersionElement(val version: Version) : CoroutineContext.Element {
        companion object Key : CoroutineContext.Key<VersionElement>

        override val key: CoroutineContext.Key<*>
            get() = Key
    }

    public companion object {
        /**
         * Retrieves the version from the coroutine context.
         *
         * **API Note**:
         * You shouldn't call this function yourself unless you use it to define your own extensions
         * that should be dependent on it.
         *
         * **Failure note**:
         * 1) if you didn't call it yourself, probably you need to register `RequestVersionProvider` by
         * putting it in the preprocessors or by using `versioning` function in `RoutingBuilder`.
         * 2) if function wasn't called by you intentionally and `RequestVersionProvider` is already
         * registered, but inside `test` you should provide content serializer to context using
         * [com.y9vad9.rsocket.router.test.preprocess] or [com.y9vad9.rsocket.router.intercepts] functions.
         *
         * @return The version extracted from the coroutine context.
         * @throws IllegalStateException if the RequestVersionProvider was not registered or called from an illegal context.
         */
        @InternalVersioningApi
        public suspend fun getFromCoroutineContext(): Version {
            return coroutineContext[VersionElement]?.version
                ?: error("RequestVersionProvider was not registered or call happened from illegal context.")
        }

        /**
         * Retrieves the version from the payload and adds it as a context element to the coroutine context.
         *
         * **Note**: You should use this function inside tests to provide version.
         *
         * @param version The version extracted from the payload.
         * @return The updated coroutine context with the version as a context element.
         */
        @ExperimentalVersioningApi
        @InternalVersioningApi
        public suspend fun asContextElement(version: Version): CoroutineContext =
            coroutineContext + VersionElement(version)
    }

    /**
     * Retrieves the version from the specified payload.
     *
     * @param payload The payload containing the version information.
     * @return The version extracted from the payload.
     */
    public abstract fun version(payload: Payload, coroutineContext: CoroutineContext): Version

    final override fun intercept(coroutineContext: CoroutineContext, input: Payload): CoroutineContext {
        return coroutineContext + VersionElement(version(input, coroutineContext))
    }
}