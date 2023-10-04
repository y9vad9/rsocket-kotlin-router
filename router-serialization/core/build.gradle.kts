plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
}

dependencies {
    commonMainImplementation(libs.rsocket.server)

    commonMainImplementation(projects.routerCore)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.rsocket.router",
        artifactId = "router-serialization-core",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing
    )

    pom {
        name.set("Router Serialization Core")
        description.set(
            """
            Kotlin RSocket library for type-safe serializable routing. Provides extensions for routing builder and
            abstraction to serialize/deserialize data.
            """.trimIndent()
        )
    }
}