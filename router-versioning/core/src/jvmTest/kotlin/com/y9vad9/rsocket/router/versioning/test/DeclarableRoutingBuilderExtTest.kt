package com.y9vad9.rsocket.router.versioning.test

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.router
import com.y9vad9.rsocket.router.test.requestResponseOrAssert
import com.y9vad9.rsocket.router.test.routeAtOrAssert
import com.y9vad9.rsocket.router.versioning.Version
import com.y9vad9.rsocket.router.versioning.builders.version
import com.y9vad9.rsocket.router.versioning.preprocessor.RequestVersionProvider
import com.y9vad9.rsocket.router.versioning.requestResponseV
import com.y9vad9.rsocket.router.versioning.versioning
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalInterceptorsApi::class)
class DeclarableRoutingBuilderExtTest {
    private val thirdRequestResponse = Payload(ByteReadPacket("test".toByteArray()))

    private val router = router {
        routeProvider { error("Stub!") }
        versioning { _, _ -> error("Stub!") }

        routing {
            route("test") {
                requestResponseV {
                    version(2) { _ ->
                        Payload.Empty
                    }

                    version(3) { _ ->
                        return@version thirdRequestResponse
                    }
                }
            }
        }
    }

    // это ахуенно
    @Test
    fun `test invalid version should fail`(): Unit = runBlocking {
        // GIVEN
        val context = RequestVersionProvider.VersionElement(Version(1, 0))

        // THEN
        assertFailsWith<RSocketError.Rejected> {
            withContext(context) {
                router.routeAtOrAssert("test")
                    .requestResponse(payload = Payload.Empty)
            }
        }
    }

    @Test
    fun `test 2 version should pass within range`(): Unit = runBlocking {
        // GIVEN
        val contexts = listOf(
            RequestVersionProvider.VersionElement(Version(2, 0)),
            RequestVersionProvider.VersionElement(Version(2, 1)),
            RequestVersionProvider.VersionElement(Version(2, 9)),
        )

        // WHEN
        repeat(contexts.size) { time ->
            withContext(contexts[time]) {
                val result = router.routeAtOrAssert("test")
                    .requestResponseOrAssert(payload = Payload.Empty)

                // THEN
                assertEquals(
                    expected = Payload.Empty,
                    actual = result,
                )
            }
        }
    }

    @Test
    fun `test 3 version should be correct`(): Unit = runBlocking {
        // GIVEN
        val contexts = listOf(
            RequestVersionProvider.VersionElement(Version(3, 0)),
            RequestVersionProvider.VersionElement(Version(3, 9)),
            RequestVersionProvider.VersionElement(Version(4, 0)),
        )

        repeat(contexts.size) { time ->
            withContext(contexts[time]) {
                val result = router.routeAtOrAssert("test")
                    .requestResponseOrAssert(payload = Payload.Empty)

                assertEquals(
                    expected = thirdRequestResponse,
                    actual = result,
                )
            }
        }
    }
}