package com.y9vad9.rsocket.router.versioning

import com.y9vad9.rsocket.router.builders.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.versioning.annotations.VersioningDsl
import com.y9vad9.rsocket.router.versioning.builders.VersioningBuilder
import com.y9vad9.rsocket.router.versioning.preprocessor.RequestVersionProvider
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.flow.Flow

/**
 * Shortened variant of [requestResponseVersioned].
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.requestResponseV(
    path: String,
    block: VersioningBuilder<Payload, Payload>.() -> Unit,
): Unit = requestResponseVersioned(path, block)

public fun DeclarableRoutingBuilder.requestResponseVersioned(
    path: String,
    block: VersioningBuilder<Payload, Payload>.() -> Unit,
): Unit = route(path) {
    requestResponseVersioned(block)
}

/**
 * Creates a request-response endpoint with versioning support.
 *
 * @param path The URL path for the endpoint.
 * @param block A lambda function that configures the endpoint using a [VersioningBuilder].
 *
 * @throws IllegalStateException if the [RequestVersionProvider] is not registered.
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.requestResponseVersioned(
    block: VersioningBuilder<Payload, Payload>.() -> Unit,
) {
    val versionedRequest = VersioningBuilder<Payload, Payload>().apply(block).build()

    requestResponse { payload ->
        versionedRequest.execute(payload)
    }
}

@VersioningDsl
public fun DeclarableRoutingBuilder.requestResponseV(
    block: VersioningBuilder<Payload, Payload>.() -> Unit,
): Unit = requestResponseVersioned(block)

/**
 * Shortened variant of [requestStreamVersioned].
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.requestStreamV(
    path: String,
    block: VersioningBuilder<Payload, Flow<Payload>>.() -> Unit,
): Unit = requestStreamVersioned(path, block)


public fun DeclarableRoutingBuilder.requestStreamVersioned(
    path: String,
    block: VersioningBuilder<Payload, Flow<Payload>>.() -> Unit,
): Unit = route(path) {
    requestStreamVersioned(block)
}

/**
 * Creates a request-stream endpoint with versioning support.
 *
 * @param path The URL path for the endpoint.
 * @param block A lambda function that configures the endpoint using a [VersioningBuilder].
 *
 * @throws IllegalStateException if the [RequestVersionProvider] is not registered.
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.requestStreamVersioned(
    block: VersioningBuilder<Payload, Flow<Payload>>.() -> Unit,
) {
    val versionedRequest = VersioningBuilder<Payload, Flow<Payload>>().apply(block).build()

    requestStream { payload ->
        versionedRequest.execute(payload)
    }
}

@VersioningDsl
public fun DeclarableRoutingBuilder.requestStreamV(
    block: VersioningBuilder<Payload, Flow<Payload>>.() -> Unit,
): Unit = requestStreamVersioned(block)

/**
 * Shortened variant of [fireAndForgetVersioned].
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.fireAndForgetV(
    path: String,
    block: VersioningBuilder<Payload, Unit>.() -> Unit,
): Unit = fireAndForgetVersioned(path, block)


public fun DeclarableRoutingBuilder.fireAndForgetVersioned(
    path: String,
    block: VersioningBuilder<Payload, Unit>.() -> Unit,
): Unit = route(path) {
    fireAndForgetVersioned(block)
}

/**
 * Creates a fireAndForget endpoint with versioning support.
 *
 * @param path The URL path for the endpoint.
 * @param block A lambda function that configures the endpoint using a [VersioningBuilder].
 *
 * @throws IllegalStateException if the [RequestVersionProvider] is not registered.
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.fireAndForgetVersioned(
    block: VersioningBuilder<Payload, Unit>.() -> Unit,
) {
    val versionedRequest = VersioningBuilder<Payload, Unit>().apply(block).build()

    fireAndForget { payload ->
        versionedRequest.execute(payload)
    }
}

@VersioningDsl
public fun DeclarableRoutingBuilder.fireAndForgetV(
    block: VersioningBuilder<Payload, Unit>.() -> Unit,
): Unit = fireAndForgetVersioned(block)


/**
 * Shortened variant of [requestChannelVersioned].
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.requestChannelV(
    path: String,
    block: VersioningBuilder<PayloadStream, Flow<Payload>>.() -> Unit,
): Unit = requestChannelVersioned(path, block)

public fun DeclarableRoutingBuilder.requestChannelVersioned(
    path: String,
    block: VersioningBuilder<PayloadStream, Flow<Payload>>.() -> Unit,
): Unit = route(path) {
    requestChannelVersioned(block)
}

/**
 * Creates a request-channel endpoint with versioning support.
 *
 * @param path The URL path for the endpoint.
 * @param block A lambda function that configures the endpoint using a [VersioningBuilder].
 *
 * @throws IllegalStateException if the [RequestVersionProvider] is not registered.
 */
@VersioningDsl
public fun DeclarableRoutingBuilder.requestChannelVersioned(
    block: VersioningBuilder<PayloadStream, Flow<Payload>>.() -> Unit,
) {
    val versionedRequest = VersioningBuilder<PayloadStream, Flow<Payload>>().apply(block).build()

    requestChannel { initPayload, payloads ->
        versionedRequest.execute(PayloadStream(initPayload, payloads))
    }
}

@VersioningDsl
public fun DeclarableRoutingBuilder.requestChannelV(
    block: VersioningBuilder<PayloadStream, Flow<Payload>>.() -> Unit,
): Unit = requestChannelVersioned(block)