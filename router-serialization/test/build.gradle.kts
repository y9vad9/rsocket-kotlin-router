plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    commonMainImplementation(libs.rsocket.server)


    commonMainImplementation(projects.routerCore)
    commonMainImplementation(projects.routerCore.test)
    commonMainImplementation(projects.routerSerialization.core)

    jvmTestImplementation(libs.kotlinx.serialization.json)
    jvmTestImplementation(libs.kotlinx.serialization.cbor)
    jvmTestImplementation(libs.kotlinx.serialization.protobuf)

    jvmTestImplementation(projects.routerSerialization.json)
    jvmTestImplementation(projects.routerSerialization.cbor)
    jvmTestImplementation(projects.routerSerialization.protobuf)
    jvmTestImplementation(libs.kotlin.test)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.rsocket.router",
        artifactId = "router-serialization-test",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing
    )

    pom {
        name.set("Router Serialization Testing")
        description.set(
            """
            Kotlin RSocket library for testing type-safe serializable routes. Experimental: can be dropped or changed
             at any time.
            """.trimIndent()
        )
    }
}