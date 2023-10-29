package com.y9vad9.rsocket.router.service.proto.codegen.types

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.wire.schema.EnclosingType
import com.y9vad9.rsocket.router.service.proto.codegen.ProtoTransformer

internal object EnclosingTypeTransformer : ProtoTransformer<EnclosingType, TypeSpec> {
    override fun transform(incoming: EnclosingType): TypeSpec {
        return TypeSpec.classBuilder(incoming.name)
            .primaryConstructor(
                FunSpec.constructorBuilder().addModifiers(KModifier.PRIVATE).build()
            )
            .addTypes(incoming.nestedTypes.map { TypeTransformer.transform(it) })
            .build()
    }

}