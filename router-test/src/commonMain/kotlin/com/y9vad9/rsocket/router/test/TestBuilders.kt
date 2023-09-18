package com.y9vad9.rsocket.router.test

import com.y9vad9.rsocket.router.Router
import com.y9vad9.rsocket.router.annotations.InternalRouterApi
import com.y9vad9.rsocket.router.builders.RouterBuilder
import io.rsocket.kotlin.RSocket
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public fun emptyRSocket(): RSocket = object : RSocket {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}