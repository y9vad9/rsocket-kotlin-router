plugins {
    `kotlin-dsl`
}

group = "publish-library"
version = "SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins.register("publish-library") {
        id = "publish-library"
        implementationClass = "com.y9vad9.maven.publish.DeployPlugin"
    }
}

dependencies {
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    //implementation("org.hidetake:gradle-ssh-plugin:2.11.2")
}