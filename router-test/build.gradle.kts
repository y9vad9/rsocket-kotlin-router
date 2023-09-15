import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.library.publish)
}

kotlin {
    jvm()

    explicitApi = ExplicitApiMode.Strict
}

dependencies {
    commonMainImplementation(libs.rsocket.server)
    commonMainImplementation(libs.rsocket.server.websockets)

    commonMainImplementation(projects.routerCore)

    commonMainImplementation(libs.kotlinx.coroutines)
    commonTestImplementation(libs.kotlin.test)
}

deployLibrary {
    ssh(tag = "maven.y9vad9.com") {
        host = System.getenv("SSH_HOST")
        user = System.getenv("SSH_USER")
        password = System.getenv("SSH_PASSWORD")
        deployPath = System.getenv("SSH_DEPLOY_PATH")

        group = "com.y9vad9.rsocket.router"
        componentName = "kotlin"
        artifactId = "router-test"
        name = "router-test"

        description = "Kotlin RSocket library for routing"

        version = System.getenv("LIB_VERSION")
    }
}