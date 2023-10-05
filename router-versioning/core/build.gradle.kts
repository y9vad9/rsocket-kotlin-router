plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
}

dependencies {
    commonMainImplementation(libs.rsocket.server)
    commonMainImplementation(projects.routerCore)

    jvmTestImplementation(libs.kotlin.test)
    jvmTestImplementation(projects.routerCore.test)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.rsocket.router",
        artifactId = "router-versioning-core",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing
    )

    pom {
        name.set("Router Versioning")
        description.set(
            """
            Kotlin RSocket library for version-safe routing. Provides semantic versioning mechanism for ensuring
             backward and forward compatibility.
            """.trimIndent()
        )
    }
}