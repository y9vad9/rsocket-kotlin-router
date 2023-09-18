package com.y9vad9.rsocket.router.builders.impl

import com.y9vad9.rsocket.router.Route
import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.builders.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.builders.RoutingBuilder
import com.y9vad9.rsocket.router.interceptors.Preprocessor
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor

@OptIn(ExperimentalInterceptorsApi::class)
internal class RoutingBuilderScopeImpl constructor(
    private val separator: Char,
    private val sharedInterceptors: List<RouteInterceptor>,
    private val preprocessors: List<Preprocessor>,
) : RoutingBuilder {
    private val subRoutes = mutableMapOf<String, Route>()

    override fun route(route: String, block: DeclarableRoutingBuilder.() -> Unit) {
        subRoutes += DeclarableRoutingBuilderScopeImpl(
            path = route,
            separator = separator,
            inheritedInterceptors = sharedInterceptors,
            preprocessors = preprocessors,
        ).apply(block).build()
    }

    fun build(): Map<String, Route> = subRoutes.toMap()
}