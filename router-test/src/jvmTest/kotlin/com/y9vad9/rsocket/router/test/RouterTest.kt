package com.y9vad9.rsocket.router.test

import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.interceptors.RouteInterceptor
import com.y9vad9.rsocket.router.router
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalRouterApi::class)
class RouterTest {
    private class MyInterceptor : RouteInterceptor.CoroutineContext {

        data class SomeCoroutineContextElement(
            val value: String,
        ) : CoroutineContext.Element {
            companion object Key : CoroutineContext.Key<SomeCoroutineContextElement>

            override val key: CoroutineContext.Key<*>
                get() = Key
        }
        override fun intercept(coroutineContext: CoroutineContext, input: Payload): CoroutineContext {
            return coroutineContext + SomeCoroutineContextElement("test")
        }
    }

    private val router = router {
        routeSeparator = '.'
        sharedInterceptors {
            forCoroutineContext(MyInterceptor())
        }

        routeProvider { error("Stub!") }

        routing {
            route("test") {
                route("subroute") {
                    fireAndForget {
                        assertEquals(expected = "test", actual = it.data.readText())
                        assertNotNull(currentCoroutineContext()[MyInterceptor.SomeCoroutineContextElement]?.value)
                    }
                }
            }
        }
    }

    @Test
    fun testRoutes() {
        runBlocking {
            val route1 = router.routeAtOrAssert("test")
            val route2 = router.routeAtOrAssert("test.subroute")

            route1.assertHasInterceptor<MyInterceptor>()
            route2.assertHasInterceptor<MyInterceptor>()

            route2.fireAndForgetOrAssert(emptyRSocket(), buildPayload {
                data("test")
            })
        }
    }
}