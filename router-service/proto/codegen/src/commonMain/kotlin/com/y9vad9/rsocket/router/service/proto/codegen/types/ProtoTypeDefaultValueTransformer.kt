package com.y9vad9.rsocket.router.service.proto.codegen.types

import com.squareup.wire.schema.ProtoType
import com.y9vad9.rsocket.router.service.proto.codegen.ProtoTransformer

internal object ProtoTypeDefaultValueTransformer : ProtoTransformer<ProtoType, String> {
    override fun transform(incoming: ProtoType): String {
        return when (incoming) {
            in listOf(ProtoType.INT32, ProtoType.INT64, ProtoType.DURATION, ProtoType.UINT32, ProtoType.UINT64) -> "0"
            ProtoType.STRING -> "\"\""
            ProtoType.BOOL -> "false"
            else -> "null"
        }
    }

}