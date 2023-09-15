package com.y9vad9.rsocket.router.builders

import io.ktor.utils.io.core.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.RSocketRequestHandlerBuilder
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.read
import com.y9vad9.rsocket.router.annotations.RouterDsl
import com.y9vad9.rsocket.router.router

/**
 * Interface for building routes for RSocket requests.
 */
@RouterDsl
public interface RoutingBuilder {
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
    router {
        this.routeSeparator = routeSeparator
        routeProvider(routeProvider)

        routing {
            builder()
        }
    }
}


