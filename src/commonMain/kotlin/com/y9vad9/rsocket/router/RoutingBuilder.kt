package com.y9vad9.rsocket.router

import io.ktor.utils.io.core.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.RSocketRequestHandlerBuilder
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.read
import io.rsocket.kotlin.payload.Payload
import com.y9vad9.rsocket.router.impl.RSocketRouter
import kotlinx.coroutines.flow.Flow

/**
 * Interface for building routes for RSocket requests.
 */
@RouterDsl
public interface RoutingBuilder {
    /**
     * The route separator character used in the application.
     *
     * This character is used to separate different parts of a route string.
     * It allows for hierarchical organization in the navigation or routing of the application.
     *
     * @return The route separator character, which is a period ('.').
     */
    public val routeSeparator: Char get() = '.'

    /**
     * Registers interceptor for current route. Depending on [strategy] will
     * also include children to be intercepted.
     *
     * **Experimental** due to considering better design for API.
     */
    @ExperimentalRouterApi
    public fun interceptors(
        builder: InterceptorsBuilder.() -> Unit,
    )

    /**
     * Defines a route for the RSocket request.
     *
     * @param route The string representation of the route.
     * @param block The lambda function to be executed when the route is called.
     *              It takes a request data ([Payload]) as input and returns response as a [Payload].
     */
    @RouterDsl
    public fun route(route: String, block: DeclarableRoutingBuilder.() -> Unit)
}

// -- builders --

/**
 * Configures routing for RSocket request handler.
 *
 * @param routeProvider The route provider function that determines the route based on the incoming request. The default implementation reads the routing metadata from the request and returns the first tag. If no route is provided, it throws an Invalid RSocketError.
 * @param routeSeparator The separator character used to separate segments in the route. The default is '.' (dot).
 * @param builder The routing configuration builder that defines the request-response, request-stream, request-channel, and fire-and-forget routes.
 */
@RouterDsl
public fun RSocketRequestHandlerBuilder.routing(
    routeProvider: suspend (metadata: ByteReadPacket?) -> String = @ExperimentalMetadataApi {
        it?.read(RoutingMetadata)?.tags?.firstOrNull()
            ?: throw RSocketError.Invalid("Route was not provided.")
    },
    routeSeparator: Char = '.',
    builder: RoutingBuilder.() -> Unit,
) {
    val requestResponses: MutableMap<String, suspend RSocket.(Payload) -> Payload> =
        mutableMapOf()
    val requestStreams: MutableMap<String, suspend RSocket.(Payload) -> Flow<Payload>> =
        mutableMapOf()
    val requestChannels: MutableMap<String, suspend RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>> =
        mutableMapOf()
    val fireAndForgets: MutableMap<String, suspend RSocket.(Payload) -> Unit> =
        mutableMapOf()

    builder(
        RSocketRouter(
            requestResponses = requestResponses,
            requestStreams = requestStreams,
            requestChannels = requestChannels,
            fireAndForgets = fireAndForgets,
            currentRoute = "",
            routeSeparator = routeSeparator,
        )
    )

    requestResponse { payload ->
        requestResponses[routeProvider(payload.metadata)]?.invoke(this, payload)
            ?: throwRouteNotFound()
    }

    requestStream { payload ->
        requestStreams[routeProvider(payload.metadata)]?.invoke(this, payload)
            ?: throwRouteNotFound()
    }

    requestChannel { initPayload, payloads ->
        requestChannels[routeProvider(initPayload.metadata)]?.invoke(this, initPayload, payloads)
            ?: throwRouteNotFound()
    }

    fireAndForget { payload ->
        fireAndForgets[routeProvider(payload.metadata)]?.invoke(this, payload)
            ?: throwRouteNotFound()
    }
}

/**
 * Throws an exception indicating that the incoming route is not defined (not found).
 *
 * @throws RSocketError.Invalid if the route is invalid.
 */
@Throws(RSocketError.Custom::class)
private fun throwRouteNotFound(): Nothing = throw RSocketError.Invalid("Route is invalid.")

