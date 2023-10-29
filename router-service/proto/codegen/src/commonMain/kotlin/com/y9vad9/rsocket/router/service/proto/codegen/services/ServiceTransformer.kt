package com.y9vad9.rsocket.router.service.proto.codegen.services

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.wire.schema.Service
import com.y9vad9.rsocket.router.service.proto.codegen.ProtoTransformer
import com.y9vad9.rsocket.router.service.proto.codegen.protoTransformer

internal object ServiceTransformer : ProtoTransformer<Service, TypeSpec> {
    override fun transform(incoming: Service): TypeSpec {
        return TypeSpec.classBuilder(incoming.name)
            .addKdoc(incoming.documentation)
            .addProperty(
                PropertySpec.builder("identifier", STRING)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("\"${incoming.name}\"")
                    .build()
            )
            .addFunctions(incoming.rpcs.map(RpcTransformer::transform))
            .build()
    }
}