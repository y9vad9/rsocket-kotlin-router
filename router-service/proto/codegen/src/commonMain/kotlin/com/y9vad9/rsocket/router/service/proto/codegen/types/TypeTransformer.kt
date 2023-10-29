package com.y9vad9.rsocket.router.service.proto.codegen.types

import com.squareup.kotlinpoet.TypeSpec
import com.squareup.wire.schema.EnclosingType
import com.squareup.wire.schema.EnumType
import com.squareup.wire.schema.MessageType
import com.squareup.wire.schema.Type
import com.y9vad9.rsocket.router.service.proto.codegen.ProtoTransformer

internal object TypeTransformer : ProtoTransformer<Type, TypeSpec> {
    override fun transform(incoming: Type): TypeSpec {
        return when (incoming) {
            is MessageType -> MessageTypeTransformer.transform(incoming)
            is EnumType -> EnumTypeTransformer.transform(incoming)
            is EnclosingType -> EnclosingTypeTransformer.transform(incoming)
        }
    }

}