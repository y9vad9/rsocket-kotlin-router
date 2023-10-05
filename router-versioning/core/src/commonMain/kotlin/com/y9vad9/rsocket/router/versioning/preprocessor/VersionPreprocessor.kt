package com.y9vad9.rsocket.router.versioning.preprocessor

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.versioning.Version
import io.rsocket.kotlin.payload.Payload
import kotlin.coroutines.CoroutineContext

@ExperimentalInterceptorsApi
public abstract class VersionPreprocessor : Preprocessor.CoroutineContext {
    internal data class VersionElement(val version: Version) : CoroutineContext.Element {
        companion object Key : CoroutineContext.Key<VersionElement>

        override val key: CoroutineContext.Key<*>
            get() = Key
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