plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
}

dependencies {
    commonMainImplementation(libs.rsocket.server)
    commonMainImplementation(libs.kotlinx.serialization)

    commonMainImplementation(projects.routerVersioning.core)
    commonMainImplementation(projects.routerSerialization.core)

    commonMainImplementation(projects.routerCore)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.rsocket.router",
        artifactId = "router-versioning-serialization",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing
    )

    pom {
        name.set("Router Versioning Serialization Adapter")
        description.set("""
            Kotlin RSocket library for supporting serialization mechanism in versioned routes.
            """.trimIndent()
        )
    }
}