plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(libs.kotlin.plugin)
    api(libs.vanniktech.maven.publish)
}