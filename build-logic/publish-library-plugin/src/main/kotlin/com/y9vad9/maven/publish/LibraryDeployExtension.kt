package com.y9vad9.maven.publish

import com.y9vad9.maven.publish.annotation.PublishDsl

/**
 * A DSL extension class for configuring library deployments.
 */
@PublishDsl
open class LibraryDeployExtension {
    /**
     * Internal mutable map that stores the deployment targets.
     */
    internal val targets: MutableMap<String, SshMavenDeployScope> = mutableMapOf()

    /**
     * Defines a new deployment target with the given [tag] and configuration [block].
     *
     * Example usage:
     * ```
     * ssh("maven.timemates.io") {
     *     host = "localhost"
     *     componentName = "my-library"
     *     group = "com.example"
     *     artifactId = "my-library"
     *     version = "1.0.0"
     *     deployPath = "/path/to/deployment"
     * }
     * ```
     *
     * @param tag The tag associated with the deployment target.
     * @param block The configuration block to customize the deployment target.
     */
    fun ssh(tag: String, block: SshMavenDeployScope.() -> Unit) {
        targets[tag] = SshMavenDeployScope().apply(block)
    }
}
