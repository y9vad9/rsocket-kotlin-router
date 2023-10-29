plugins {
    id(libs.plugins.multiplatform.module.convention.get().pluginId)
}

dependencies {
    commonMainImplementation(libs.wire.schema)
    commonMainImplementation(libs.squareup.kotlinpoet)
    commonMainImplementation(libs.squareup.okio)
}