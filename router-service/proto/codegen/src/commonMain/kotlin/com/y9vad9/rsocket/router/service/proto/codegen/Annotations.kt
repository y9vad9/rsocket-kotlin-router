package com.y9vad9.rsocket.router.service.proto.codegen

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName

internal object Annotations {
    fun protoNumber(number: Int): AnnotationSpec =
        AnnotationSpec.builder(
            ClassName("kotlinx.serialization.protobuf", "ProtoNumber")
        ).addMember(number.toString()).build()

    val serializable = AnnotationSpec.builder(ClassName("kotlinx.serialization", "Serializable")).build()
}