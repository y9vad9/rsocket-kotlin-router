package com.y9vad9.rsocket.router

import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.payload.Payload
import kotlinx.coroutines.flow.Flow

/**
 * A routing builder with the ability to provide methods at specific routes.
 */
public interface DeclarableRoutingBuilder : RoutingBuilder {
    /**
     * Makes a request to the RSocket server and waits for the response.
     *
     * @param block A suspend lambda that takes an RSocket instance and a Payload as input and returns a Payload.
     *              This lambda is responsible for processing the payload and generating the response.
     * @return The response Payload from the server.
     */
    public fun requestResponse(block: suspend RSocket.(payload: Payload) -> Payload)

    /**
     * Makes a stream request to the RSocket with the provided [Payload] and returns a [Flow] of [Payload] as the response.
     *
     * @param block The block of suspended lambda code to execute, taking the RSocket instance and the input [Payload] as parameters,
     *              and returning a [Flow] of [Payload].
     *              The lambda is responsible for handling the stream logic and emitting values to the flow.
     *              The flow will be automatically cancelable when not required anymore.
     *
     * @return A [Flow] of [Payload] representing the stream of response payloads received from the server.
     *
     * @throws Exception if any error occurs during the stream request or handling.
     */
    public fun requestStream(block: suspend RSocket.(payload: Payload) -> Flow<Payload>)

    /**
     * Requests a channel within RSocket in current route.
     *
     * @param block The block of suspended code to be executed, which takes two parameters:
     *              - initPayload: The initial payload for the channel.
     *              - payloads: The flow of payloads to be sent to the channel.
     *              The block should return a flow of payloads received from the channel.
     * @return A flow of payloads received from the channel.
     */
    public fun requestChannel(block: suspend (RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>))

    /**
     * Executes the given [block] in a fire-and-forget manner.
     *
     * This method is used to send a single request without requiring a response. The [block]
     * takes an RSocket instance and a Payload object as parameters, allowing you to perform
     * any necessary operations within the block.
     *
     * @param block the suspend block to be executed in a fire-and-forget manner.
     *              It takes an RSocket instance and a Payload object as parameters.
     *              The block is responsible for processing the Payload object accordingly.
     */
    public fun fireAndForget(block: suspend RSocket.(payload: Payload) -> Unit)
}


// -- extensions --

public fun DeclarableRoutingBuilder.requestResponse(
    route: String,
    block: suspend RSocket.(payload: Payload) -> Payload
): Unit = route(route) {
    requestResponse(block)
}

public fun DeclarableRoutingBuilder.requestChannel(
    route: String,
    block: suspend (RSocket.(initPayload: Payload, payloads: Flow<Payload>) -> Flow<Payload>)
): Unit = route(route) {
    requestChannel(block)
}

public fun DeclarableRoutingBuilder.requestStream(
    route: String,
    block: suspend RSocket.(payload: Payload) -> Flow<Payload>
): Unit = route(route) {
    requestStream(block)
}

public fun DeclarableRoutingBuilder.fireAndForget(
    route: String,
    block: suspend RSocket.(payload: Payload) -> Unit
): Unit = route(route) {
    fireAndForget(block)
}