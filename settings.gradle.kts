enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.y9vad9.com")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "rsocket-kotlin-router"

includeBuild("build-conventions")

include(":router-core", ":router-core:test")
include(
    ":router-versioning:core",
    ":router-versioning:serialization",
)
include(
    ":router-serialization:core",
    ":router-serialization:json",
    ":router-serialization:protobuf",
    ":router-serialization:cbor",
    ":router-serialization:test",
)

include(":router-service:core", ":router-service:proto:codegen")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}