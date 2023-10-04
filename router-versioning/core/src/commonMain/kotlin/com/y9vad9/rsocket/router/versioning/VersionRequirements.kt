package com.y9vad9.rsocket.router.versioning

/**
 * Class representing the requirements for a version range.
 *
 * @property firstAcceptableVersion The first acceptable version in the range.
 * @property lastAcceptableVersion The last acceptable version in the range.
 */
public data class VersionRequirements(
    val firstAcceptableVersion: Version,
    val lastAcceptableVersion: Version,
) {
    /**
     * Checks if the given version satisfies the acceptable version range.
     *
     * @param version The version to be checked against the acceptable version range.
     * @return true if the given version is within the acceptable range, false otherwise.
     */
    public fun satisfies(version: Version): Boolean =
        version in firstAcceptableVersion until lastAcceptableVersion
}