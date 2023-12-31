plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
    `maven-publish`
}

group = "com.y9vad9.rsocket.router"
version = System.getenv("LIB_VERSION") ?: "SNAPSHOT"


dependencies {
    commonMainImplementation(libs.rsocket.server)

    commonMainImplementation(libs.kotlinx.coroutines)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.rsocket.router",
        artifactId = "router-core",
        version = System.getenv("LIB_VERSION")  ?: return@mavenPublishing,
    )

    pom {
        name.set("Router Core")
        description.set("Kotlin RSocket library for routing management.")
    }
}