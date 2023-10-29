package com.y9vad9.rsocket.router.service.proto.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

internal object Types {
    fun flow(ofTypeName: TypeName): ParameterizedTypeName =
        ClassName("kotlinx.coroutines.flow", "Flow")
            .parameterizedBy(ofTypeName)

    fun list(ofTypeName: TypeName): ParameterizedTypeName =
        ClassName("kotlin.collections", "List")
            .parameterizedBy(ofTypeName)

    val any: TypeName = ClassName("kotlin", "Any")
}