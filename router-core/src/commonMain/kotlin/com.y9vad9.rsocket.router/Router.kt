package com.y9vad9.rsocket.router

import io.ktor.utils.io.core.*
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.RSocketRequestHandlerBuilder
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.annotations.InternalRouterApi
import com.y9vad9.rsocket.router.annotations.RouterDsl
import com.y9vad9.rsocket.router.builders.RouterBuilder
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor

/**
 * The RSocket router with all registered routes, configurations, preprocessors
 * and interceptors.
 */
public sealed interface Router {
    /**
     * The route separator character used in the application.
     *
     * This character is used to separate different parts of a route string.
     * It allows for hierarchical organization in the navigation or routing of the application.
     */
    public val routeSeparator: Char

    /**
     * The list of preprocessors that is registered for current router.
     *
     * Preprocessors are always run before any processing from router. They're
     * experimental due to considerations of better API.
     */
    @ExperimentalRouterApi
    public val preprocessors: List<Preprocessor<*, *>>

    /**
     * The list of interceptors that are shared to all the routes.
     */
    @ExperimentalRouterApi
    public val sharedInterceptors: List<RouteInterceptor<*, *>>

    /**
     * Retrieves route based on given [path].
     */
    public fun routeAt(path: String): Route?

    @InternalRouterApi
    public suspend fun getRoutePathFromMetadata(metadata: ByteReadPacket?): String
}

// -- builders --

@OptIn(ExperimentalRouterApi::class, InternalRouterApi::class)
@RouterDsl
public fun RSocketRequestHandlerBuilder.router(builder: RouterBuilder.() -> Unit): Router {
    val router = RouterBuilder().apply(builder).build()

    requestResponse { payload ->
        router.routeAtOrFail(router.getRoutePathFromMetadata(payload.metadata))
            .requestResponseOrThrow(this, payload)
    }

    requestStream { payload ->
        router.routeAtOrFail(router.getRoutePathFromMetadata(payload.metadata))
            .requestStreamOrThrow(this, payload)
    }

    requestChannel { initPayload, payloads ->
        router.routeAtOrFail(router.getRoutePathFromMetadata(initPayload.metadata))
            .requestChannelOrThrow(this, initPayload, payloads)
    }

    fireAndForget { payload ->
        router.routeAtOrFail(router.getRoutePathFromMetadata(payload.metadata))
            .fireAndForgetOrThrow(this, payload)
    }

    return router
}


// -- extensions --

@ExperimentalRouterApi
@Throws(RSocketError.Invalid::class)
public fun Router.routeAtOrFail(path: String): Route =
    routeAt(path) ?: throw RSocketError.Invalid("Route `$path` is not found.")


// -- internal implementation --

internal class RouterImpl @ExperimentalRouterApi constructor(
    override val routeSeparator: Char,
    @property:ExperimentalRouterApi
    override val preprocessors: List<Preprocessor<*, *>>,
    @property:ExperimentalRouterApi
    override val sharedInterceptors: List<RouteInterceptor<*, *>>,
    private val routes: Map<String, Route>,
    private var routeProvider: suspend (metadata: ByteReadPacket?) -> String,
) : Router {
    override fun routeAt(path: String): Route? {
        return routes[path]
    }

    @InternalRouterApi
    override suspend fun getRoutePathFromMetadata(metadata: ByteReadPacket?): String {
        return routeProvider(metadata)
    }
}