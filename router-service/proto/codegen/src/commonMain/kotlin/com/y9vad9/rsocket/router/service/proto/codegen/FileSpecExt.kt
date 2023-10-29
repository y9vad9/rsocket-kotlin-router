package com.y9vad9.rsocket.router.service.proto.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

internal fun FileSpec.Builder.addTypes(types: List<TypeSpec>): FileSpec.Builder = apply {
    types.forEach {
        addType(it)
    }
}