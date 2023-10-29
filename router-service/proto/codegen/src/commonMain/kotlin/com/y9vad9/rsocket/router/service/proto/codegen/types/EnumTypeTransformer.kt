package com.y9vad9.rsocket.router.service.proto.codegen.types

import com.squareup.kotlinpoet.TypeSpec
import com.squareup.wire.schema.EnumType
import com.y9vad9.rsocket.router.service.proto.codegen.Annotations
import com.y9vad9.rsocket.router.service.proto.codegen.ProtoTransformer

internal object EnumTypeTransformer : ProtoTransformer<EnumType, TypeSpec> {
    override fun transform(incoming: EnumType): TypeSpec {
        return TypeSpec.enumBuilder(incoming.name)
            .addAnnotation(Annotations.serializable)
            .apply {
                incoming.constants.forEach { constant ->
                    addEnumConstant(
                        constant.name,
                        TypeSpec.anonymousClassBuilder().addKdoc(constant.documentation)
                            .addAnnotation(Annotations.protoNumber(constant.tag))
                            .build()
                    )
                }
            }.addTypes(incoming.nestedTypes.map { TypeTransformer.transform(it) }).build()
    }

}