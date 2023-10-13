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
     * @param from From which version to be applied.
     * @param until Until which version should be applied.
     * @param block The coroutine block that will be executed for the given version.
     */
    public fun version(
        from: Version,
        until: Version = Version.INDEFINITE,
        block: suspend (T) -> R,
    ) {
        versionedRequest = when (val versionedRequest = versionedRequest) {
            null -> {
                VersionedRequest.SingleConditional(
                    function = block,
                    VersionRequirements(firstAcceptableVersion = from, lastAcceptableVersion = until)
                )
            }

            is VersionedRequest.SingleConditional<T, R> -> {
                VersionedRequest.MultipleConditional(
                    variants = listOf(
                        versionedRequest.versionRequirements.copy(
                            lastAcceptableVersion = (versionedRequest.versionRequirements.firstAcceptableVersion until from).endInclusive
                        ) to versionedRequest.function,
                        VersionRequirements(from, Version.INDEFINITE) to block,
                    )
                )
            }

            is VersionedRequest.MultipleConditional<T, R> -> {
                versionedRequest.copy(
                    variants = (versionedRequest.variants.mapIndexed { index, (requirements, function) ->
                        if (index == versionedRequest.variants.lastIndex)
                            requirements.copy(
                                lastAcceptableVersion = (requirements.firstAcceptableVersion until from).endInclusive,
                            ) to function
                        else requirements to function
                    } + (VersionRequirements(from, until) to block)).sortedBy {
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
 * @param fromMajor The major version number from which variant will be applied.
 * @param untilMajor The major version number until which variant will be applied.
 * @param block The block of code to be executed for the given version.
 */
public fun <T, R> VersioningBuilder<T, R>.version(
    fromMajor: Int,
    untilMajor: Int = Int.MAX_VALUE,
    block: suspend (T) -> R,
) {
    version(
        from = Version(major = fromMajor, minor = 0),
        until = Version(major = untilMajor, minor = 0),
        block = block,
    )
}