plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
}

group = "com.y9vad9.rsocket.router"
version = System.getenv("LIB_VERSION") ?: "SNAPSHOT"

dependencies {
    commonMainImplementation(libs.rsocket.server)

    commonMainImplementation(projects.routerCore)

    commonMainImplementation(libs.kotlinx.coroutines)
    commonTestImplementation(libs.kotlin.test)
}

mavenPublishing {
    coordinates(
        groupId = "com.y9vad9.rsocket.router",
        artifactId = "router-test",
        version = System.getenv("LIB_VERSION") ?: return@mavenPublishing,
    )

    pom {
        name.set("Router Test")
        description.set("Library for testing rsocket routes.")
    }
}