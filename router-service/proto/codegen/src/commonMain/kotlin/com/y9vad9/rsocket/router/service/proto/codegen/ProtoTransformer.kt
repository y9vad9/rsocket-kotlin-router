package com.y9vad9.rsocket.router.service.proto.codegen

internal fun interface ProtoTransformer<TInput, TResult> {
    fun transform(incoming: TInput): TResult
}

internal fun <TInput, TResult> protoTransformer(
    block: ProtoTransformer<TInput, TResult>,
): ProtoTransformer<TInput, TResult> {
    return block
}
