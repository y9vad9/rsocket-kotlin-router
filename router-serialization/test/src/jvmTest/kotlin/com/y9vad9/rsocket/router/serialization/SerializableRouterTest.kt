package com.y9vad9.rsocket.router.serialization

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.router
import com.y9vad9.rsocket.router.serialization.annotations.ExperimentalRouterSerializationApi
import com.y9vad9.rsocket.router.serialization.json.JsonContentSerializer
import com.y9vad9.rsocket.router.serialization.preprocessor.SerializationProvider
import com.y9vad9.rsocket.router.serialization.preprocessor.serialization
import com.y9vad9.rsocket.router.serialization.test.requestResponseOrAssert
import com.y9vad9.rsocket.router.test.requestResponseOrAssert
import com.y9vad9.rsocket.router.test.routeAtOrAssert
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.RoutingMetadata.Reader.read
import io.rsocket.kotlin.metadata.read
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalRouterSerializationApi::class)
class SerializableRouterTest {
    @Serializable
    private data class Foo(val bar: Int)

    @Serializable
    private data class Bar(val foo: Int)

    private val router = router {
        serialization { _, _ -> JsonContentSerializer(Json)  }
        routeProvider { _ -> TODO() }

        routing {
            route("test") {
                requestResponse<Foo, Bar> {
                    Bar(it.bar)
                }
            }
        }
    }

    @OptIn(ExperimentalInterceptorsApi::class)
    @Test
    fun `check serialization`() {
        runBlocking {
            withContext(SerializationProvider.asContextElement(JsonContentSerializer(Json))) {
                val result = router.routeAtOrAssert("test")
                    .requestResponseOrAssert<Foo, Bar>(
                        data = Foo(0),
                    )

                assertEquals(result.foo, 0)
            }
        }
    }
}