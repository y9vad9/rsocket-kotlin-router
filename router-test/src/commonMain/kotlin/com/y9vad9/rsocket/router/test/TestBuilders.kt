package com.y9vad9.rsocket.router.test

import io.rsocket.kotlin.RSocket
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public object EmptyRSocket : RSocket {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}

public fun emptyRSocket(): RSocket = EmptyRSocket