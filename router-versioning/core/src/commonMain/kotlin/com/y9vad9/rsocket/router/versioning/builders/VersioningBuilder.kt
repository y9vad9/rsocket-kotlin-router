package com.y9vad9.rsocket.router.versioning.builders

import com.y9vad9.rsocket.router.versioning.Version
import com.y9vad9.rsocket.router.versioning.VersionRequirements
import com.y9vad9.rsocket.router.versioning.VersionedRequest
import com.y9vad9.rsocket.router.versioning.until

/**
 * Builder class used for versioning requests.
 * @param T The type of the request.
 * @param R The type of the response.
 */
public class VersioningBuilder<T, R> internal constructor() {
    private var versionedRequest: VersionedRequest<T, R>? = null

    /**
     * Updates the versioned request based on the given version.
     *
     * @param version The new version to be applied.
     * @param block The coroutine block that will be executed for the given version.
     */
    public fun version(version: Version, block: suspend (T) -> R) {
        versionedRequest = when (val versionedRequest = versionedRequest) {
            null -> {
                VersionedRequest.SingleConditional(
                    function = block,
                    VersionRequirements(firstAcceptableVersion = version, lastAcceptableVersion = Version.INDEFINITE)
                )
            }

            is VersionedRequest.SingleConditional<T, R> -> {
                VersionedRequest.MultipleConditional(
                    variants = listOf(
                        versionedRequest.versionRequirements.copy(
                            lastAcceptableVersion = (versionedRequest.versionRequirements.firstAcceptableVersion until version).endInclusive
                        ) to versionedRequest.function,
                        VersionRequirements(version, Version.INDEFINITE) to block,
                    )
                )
            }

            is VersionedRequest.MultipleConditional<T, R> -> {
                versionedRequest.copy(
                    variants = (versionedRequest.variants.mapIndexed { index, (requirements, function) ->
                        if (index == versionedRequest.variants.lastIndex)
                            requirements.copy(
                                lastAcceptableVersion = (requirements.firstAcceptableVersion until version).endInclusive,
                            ) to function
                        else requirements to function
                    } + (VersionRequirements(version, Version.INDEFINITE) to block)).sortedBy {
                        it.first.firstAcceptableVersion
                    }
                )
            }
        }
    }

    internal fun build(): VersionedRequest<T, R> = versionedRequest ?: error("No version was specified.")
}

/**
 * Adds a version to the VersioningBuilder.
 *
 * @param major The major version number.
 * @param block The block of code to be executed for the given version.
 * @return Unit
 */
public fun <T, R> VersioningBuilder<T, R>.version(major: Int, block: suspend (T) -> R): Unit =
    version(Version(major, 0), block)