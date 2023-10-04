package com.y9vad9.rsocket.router.versioning

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.versioning.preprocessor.VersionPreprocessor
import kotlin.coroutines.coroutineContext

/**
 * Represents a version number.
 *
 * @param double The numerical value of the version.
 * @throws IllegalArgumentException if the version is negative.
 */
public data class Version(public val major: Int, public val minor: Int, public val patch: Int = 0) : Comparable<Version> {
    init {
        require(major >= 0) { "Major version cannot be negative" }
        require(minor >= 0) { "Minor version cannot be negative" }
        require(patch >= 0) { "Patch version cannot be negative" }
    }

    public companion object {
        /**
         * Represents the first version. In meaning of versioning, it means that
         * we accept request / route from any version.
         */
        public val ZERO: Version = Version(0, 0, 0)
        /**
         * Represents the latest version.
         *
         * In meaning of versioning, it means that request has no max version
         * and it's actual.
         *
         * @see Version
         */
        public val INDEFINITE: Version = Version(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
    }

    /**
     * Compares this version with the specified version.
     *
     * @param other the version to be compared
     * @return a negative integer, zero, or a positive integer if this version is less than, equal to, or greater than
     *         the specified version
     */
    override fun compareTo(other: Version): Int {
        return when {
            this.major != other.major -> this.major - other.major
            this.minor != other.minor -> this.minor - other.minor
            else -> this.patch - other.patch
        }
    }
}

/**
 * Creates a closed range of versions starting from this version and ending at another version.
 *
 * @param another The ending version of the range.
 * @return A closed range of versions from this version to the specified ending version.
 * @throws IllegalArgumentException If the ending version is negative.
 */
public infix fun Version.until(another: Version): ClosedRange<Version> {
    return when {
        another.patch > 0 -> this .. another.copy(patch = patch - 1)
        another.minor > 0 -> this .. another.copy(minor = minor - 1)
        another.major > 0 -> this .. another.copy(major = major - 1)
        else -> error("Unable to create `until` range – version cannot be negative.")
    }
}


/**
 * Retrieves the version of the requester.
 *
 * @return The version of the requester if available.
 * @throws IllegalStateException if the version cannot be retrieved. This can happen if the function is called from an illegal context or if the preprocessor wasn't installed.
 *
 * @since 1.0.0
 * @experimental This API is experimental and subject to change in future versions.
 */
@OptIn(ExperimentalInterceptorsApi::class)
internal suspend fun getRequesterVersion(): Version =
    coroutineContext[VersionPreprocessor.VersionElement]?.version
        ?: error("Unable to retrieve version: Preprocessor wasn't installed.")