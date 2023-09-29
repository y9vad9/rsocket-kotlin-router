package com.y9vad9.rsocket.router

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import io.ktor.utils.io.core.*
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.annotations.InternalRouterApi
import com.y9vad9.rsocket.router.annotations.RouterDsl
import com.y9vad9.rsocket.router.builders.RouterBuilder
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor
import io.rsocket.kotlin.*
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext

/**
 * The RSocket router with all registered routes, configurations, preprocessors
 * and interceptors.
 */
public interface Router {
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
    @ExperimentalInterceptorsApi
    public val preprocessors: List<Preprocessor>

    /**
     * The list of interceptors that are shared to all the routes.
     */
    @ExperimentalInterceptorsApi
    public val sharedInterceptors: List<RouteInterceptor>

    /**
     * Retrieves route based on given [path].
     */
    public fun routeAt(path: String): Route?

    @InternalRouterApi
    public suspend fun getRoutePathFromMetadata(metadata: ByteReadPacket?): String
}

// -- builders --

@RouterDsl
public fun RSocketRequestHandlerBuilder.router(block: RouterBuilder.() -> Unit): Router {
    return router(builder = block).also { router -> router.installOn(this) }
}

@OptIn(InternalRouterApi::class)
public fun router(builder: RouterBuilder.() -> Unit): Router = RouterBuilder().apply(builder).build()

/**
 * Applies [Router] to given [RSocketRequestHandlerBuilder]. All registered routes are listened.
 *
 * **Implementation note**: As [Router] does not provide `metadataPush` feature, this function is especially
 * useful if you want to additionally provide it for your [RSocket] instance.
 */
@OptIn(ExperimentalRouterApi::class, ExperimentalInterceptorsApi::class, InternalRouterApi::class)
public fun Router.installOn(handlerBuilder: RSocketRequestHandlerBuilder): Unit = with(handlerBuilder) {
    requestResponse { payload ->
        payload.intercept(preprocessors) {
            routeAtOrFail(getRoutePathFromMetadata(it.metadata))
                .requestResponseOrThrow(it)
        }
    }

    requestStream { payload ->
        payload.intercept(preprocessors) {
            routeAtOrFail(getRoutePathFromMetadata(it.metadata))
                .requestStreamOrThrow(it)
        }
    }

    requestChannel { initPayload, payloads ->
        initPayload.intercept(preprocessors) {
            routeAtOrFail(getRoutePathFromMetadata(it.metadata))
                .requestChannelOrThrow(it, payloads)
        }
    }

    fireAndForget { payload ->
        payload.intercept(preprocessors) {
            routeAtOrFail(getRoutePathFromMetadata(it.metadata))
                .fireAndForgetOrThrow(it)
        }
    }
}

@OptIn(ExperimentalInterceptorsApi::class)
private suspend inline fun <R> Payload.intercept(preprocessors: List<Preprocessor>, crossinline block: suspend (Payload) -> R): R {
    var coroutineContext = currentCoroutineContext()

    val processed = preprocessors.fold(this) { acc, preprocessor ->
        when (preprocessor) {
            is Preprocessor.CoroutineContext -> {
                coroutineContext = preprocessor.intercept(coroutineContext, acc)
                acc
            }
            is Preprocessor.Modifier -> preprocessor.intercept(acc)
        }
    }

    return withContext(coroutineContext) {
        block(processed)
    }
}

@Suppress("UnusedReceiverParameter")
public fun ConnectionAcceptor.installRouter(router: Router): RSocket {
    return RSocketRequestHandler {
        router.installOn(this)
    }
}


// -- extensions --

@ExperimentalRouterApi
@Throws(RSocketError.Invalid::class)
public fun Router.routeAtOrFail(path: String): Route =
    routeAt(path) ?: throw RSocketError.Invalid("Route `$path` is not found.")


// -- internal implementation --

internal class RouterImpl @[ExperimentalRouterApi ExperimentalInterceptorsApi] constructor(
    override val routeSeparator: Char,
    @property:ExperimentalRouterApi
    override val preprocessors: List<Preprocessor>,
    @property:ExperimentalRouterApi
    override val sharedInterceptors: List<RouteInterceptor>,
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