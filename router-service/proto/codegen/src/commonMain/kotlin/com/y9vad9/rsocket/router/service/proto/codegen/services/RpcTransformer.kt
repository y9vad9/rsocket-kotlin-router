package com.y9vad9.rsocket.router.service.proto.codegen.services

import com.squareup.kotlinpoet.*
import com.squareup.wire.schema.ProtoType
import com.squareup.wire.schema.Rpc
import com.y9vad9.rsocket.router.service.proto.codegen.ProtoTransformer
import com.y9vad9.rsocket.router.service.proto.codegen.Types
import com.y9vad9.rsocket.router.service.proto.codegen.protoTransformer

internal val RpcTransformer: ProtoTransformer<Rpc, FunSpec> = protoTransformer { rpc ->
    val (requestType, returnType) = RpcTypeTransformer.transform(rpc)

    FunSpec.builder(rpc.name)
        .addKdoc(rpc.documentation)
        .addModifiers(KModifier.ABSTRACT)
        .addParameter(
            ParameterSpec.builder(
                "request",
                requestType,
            ).build()
        )
        .returns(returnType)
        .build()
}

private val RpcTypeTransformer: ProtoTransformer<Rpc, Pair<TypeName, TypeName>> = protoTransformer { rpc ->
    val requestClassName = RpcNameTransformer.transform(rpc.requestType)
    val responseClassName = RpcNameTransformer.transform(rpc.responseType)

    (if (rpc.requestStreaming) Types.flow(requestClassName) else requestClassName) to
        (if (rpc.responseStreaming) Types.flow(responseClassName) else responseClassName)
}

private val RpcNameTransformer: ProtoTransformer<ProtoType?, ClassName> = protoTransformer { type ->
    requireNotNull(type) { "Internal: Linker wasn't called before transformer." }

    @Suppress("NAME_SHADOWING")
    val type = type.toString()

    val packageName = type.substringBeforeLast('.')
    val className = type.substringAfterLast('.')
    ClassName(packageName, className)
}