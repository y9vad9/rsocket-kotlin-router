package com.y9vad9.rsocket.router.impl

import com.y9vad9.rsocket.router.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.ExperimentalRouterApi
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.payload.Payload
import io.timemates.backend.rsocket.routing.*
import com.y9vad9.rsocket.router.Interceptors
import com.y9vad9.rsocket.router.InterceptorsBuilder
import com.y9vad9.rsocket.router.interceptors.InterceptedRequest
import kotlinx.coroutines.flow.Flow

/**
 * Default internal implementation of routing system.
 *
 * Produced on every new route, refer to the implementation of [RSocketRouter.route].
 */
internal data class RSocketRouter(
    private val requestResponses: MutableMap<String, suspend RSocket.(Payload) -> Payload>,
    private val requestStreams: MutableMap<String, suspend RSocket.(Payload) -> Flow<Payload>>,
    private val requestChannels: MutableMap<String, suspend RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>>,
    private val fireAndForgets: MutableMap<String, suspend RSocket.(Payload) -> Unit>,
    private val currentRoute: String,
    override val routeSeparator: Char = '.',
    private val initInterceptors: Interceptors = Interceptors()
) : DeclarableRoutingBuilder {
    private var interceptors: Interceptors = initInterceptors.copy()

    override fun requestResponse(block: suspend RSocket.(Payload) -> Payload) {
        require(!requestResponses.containsKey(currentRoute)) {
            "Request-Response is already defined for '$currentRoute' route."
        }

        requestResponses[currentRoute] = {
            val request = interceptors.wrapRequest(InterceptedRequest(it, coroutineContext))
            block(this, request.payload)
        }
    }

    override fun requestStream(block: suspend RSocket.(Payload) -> Flow<Payload>) {
        require(!requestStreams.containsKey(currentRoute)) {
            "Request-Stream is already defined for '$currentRoute' route."
        }

        requestStreams[currentRoute] = {
            val request = interceptors.wrapRequest(InterceptedRequest(it, coroutineContext))
            block(this, request.payload)
        }
    }

    override fun requestChannel(block: suspend RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>) {
        require(!requestChannels.containsKey(currentRoute)) {
            "Request-Channel is already defined for '$currentRoute' route."
        }

        requestChannels[currentRoute] = { initialPayload, payloads ->
            val request = interceptors.wrapRequest(InterceptedRequest(initialPayload, coroutineContext))
            block(this, request.payload, payloads)
        }
    }

    override fun fireAndForget(block: suspend RSocket.(Payload) -> Unit) {
        require(!fireAndForgets.containsKey(currentRoute)) {
            "Fire-and-Forget is already defined for '$currentRoute' route."
        }

        fireAndForgets[currentRoute] = {
            val request = interceptors.wrapRequest(InterceptedRequest(it, coroutineContext))
            block(this, request.payload)
        }
    }

    @ExperimentalRouterApi
    override fun interceptors(builder: InterceptorsBuilder.() -> Unit) {
        val builtInterceptors = InterceptorsBuilder().apply(builder).build()
        interceptors = initInterceptors.copy(
            coroutineContextInterceptors = initInterceptors.coroutineContextInterceptors
                + builtInterceptors.coroutineContextInterceptors
        )
    }

    override fun route(route: String, block: DeclarableRoutingBuilder.() -> Unit) {
        block(
            copy(
                currentRoute = if (currentRoute.isNotEmpty())
                    currentRoute + routeSeparator + route
                else route,
                initInterceptors = interceptors,
            )
        )
    }
}