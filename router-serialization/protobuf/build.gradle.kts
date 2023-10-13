plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
}

dependencies {
    commonMainImplementation(libs.rsocket.server)
    commonMainImplementation(libs.kotlinx.serialization.protobuf)

    commonMainImplementation(projects.routerSerialization.core)
    commonMainImplementation(projects.routerCore)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.rsocket.router",
        artifactId = "router-serialization-protobuf",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing
    )

    pom {
        name.set("Router Serialization (Json)")
        description.set(
            """
            Kotlin RSocket library for type-safe serializable routing. Provides ProtoBuf implementation of `ContentSerializer`.
            """.trimIndent()
        )
    }
}