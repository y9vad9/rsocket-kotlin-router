package com.y9vad9.rsocket.router.versioning

import io.rsocket.kotlin.RSocketError
import kotlinx.coroutines.flow.Flow

/**
 * A sealed class representing versioned requests.
 * @param T The input type of the request.
 * @param R The result type of the request.
 */
internal sealed class VersionedRequest<T, R> {
    /**
     * Executes the given input and returns the result.
     *
     * @param input The input to be executed.
     * @return The result of executing the input.
     */
    abstract suspend fun execute(input: T): R

    /**
     * Executes a single conditional request that checks if the input version satisfies the version requirements.
     *
     * @param T the type of the input parameter.
     * @param R the type of the return value.
     * @property function the suspend function to be executed.
     * @property versionRequirements the version requirements that need to be satisfied.
     */
    class SingleConditional<T, R>(
        val function: suspend (T) -> R,
        val versionRequirements: VersionRequirements
    ) : VersionedRequest<T, R>() {
        override suspend fun execute(input: T): R {
            val version = getRequesterVersion()

            if (!versionRequirements.satisfies(version))
                throw RSocketError.Rejected("Request is not available for your API version.")
            return function(input)
        }
    }

    /**
     * A class that provides multiple conditional execution based on API version requirements.
     *
     * @param T The input type of the request.
     * @param R The result type of the request.
     * @property variants A list of pairs that associate version requirements with suspended functions that take input of type T and return output of type R.
     */
    data class MultipleConditional<T, R>(
        val variants: List<Pair<VersionRequirements, suspend (T) -> R>>
    ) : VersionedRequest<T, R>() {
        override suspend fun execute(input: T): R {
            val version = getRequesterVersion()

            return variants.firstOrNull { (requirement, _) ->
                requirement.satisfies(version)
            }?.second?.invoke(input) ?: throw RSocketError.Rejected("Request is not available for your API version.")
        }
    }
}

public data class PayloadStream(
    val initPayload: io.rsocket.kotlin.payload.Payload,
    val payloads: Flow<io.rsocket.kotlin.payload.Payload>,
)