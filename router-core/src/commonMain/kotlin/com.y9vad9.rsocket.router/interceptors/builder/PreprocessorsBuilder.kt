package com.y9vad9.rsocket.router.interceptors.builder

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.interceptors.Preprocessor

@ExperimentalInterceptorsApi
public class PreprocessorsBuilder internal constructor() {
    private val preprocessors = mutableListOf<Preprocessor>()

    public fun forCoroutineContext(preprocessor: Preprocessor.CoroutineContext) {
        preprocessors += preprocessor
    }

    public fun forModification(preprocessor: Preprocessor.Modifier) {
        preprocessors += preprocessor
    }

    internal fun build(): List<Preprocessor> = preprocessors.toList()
}