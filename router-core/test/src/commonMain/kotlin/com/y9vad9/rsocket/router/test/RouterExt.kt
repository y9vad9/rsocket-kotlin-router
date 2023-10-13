package com.y9vad9.rsocket.router.test

import com.y9vad9.rsocket.router.Router
import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.intercepts
import io.rsocket.kotlin.payload.Payload

/**
 * Applies a list of preprocessors available in router to a payload and then executes a block of code with the
 * processed payload.
 *
 * @param payload The payload to be preprocessed.
 * @param block The block of code to be executed after preprocessing the payload.
 * @return The result of executing the block.
 */
@OptIn(ExperimentalRouterApi::class)
@ExperimentalInterceptorsApi
public suspend fun <R> Router.preprocess(payload: Payload, block: suspend (Payload) -> R): R =
    preprocessors.intercepts(payload, block)