package com.y9vad9.rsocket.router.service.proto.codegen.types

import com.squareup.kotlinpoet.*
import com.squareup.wire.schema.MessageType
import com.y9vad9.rsocket.router.service.proto.codegen.Annotations
import com.y9vad9.rsocket.router.service.proto.codegen.ProtoTransformer
import com.y9vad9.rsocket.router.service.proto.codegen.Types

internal object MessageTypeTransformer : ProtoTransformer<MessageType, TypeSpec> {
    override fun transform(incoming: MessageType): TypeSpec {
        val parameterTypes = incoming.declaredFields.map { field ->
            val fieldType = field.type!!

            ClassName(
                fieldType.enclosingTypeOrPackage ?: "",
                fieldType.simpleName,
            ).let {
                when {
                    field.isRepeated -> Types.list(it)
                    field.isOneOf -> Types.any
                    else -> it
                }
            }.copy(nullable = !fieldType.isScalar || fieldType.isWrapper)
        }

        return TypeSpec.classBuilder(incoming.name)
            .addKdoc(incoming.documentation)
            .addAnnotation(Annotations.serializable)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addModifiers(KModifier.PRIVATE)
                    .addParameters(incoming.declaredFields.mapIndexed { index, field ->
                        val type = parameterTypes[index]

                        ParameterSpec.builder(field.name, type)
                            .defaultValue(
                                if (type.isNullable)
                                    "null"
                                else field.default ?: ProtoTypeDefaultValueTransformer.transform(incoming.type)
                            )
                            .build()
                    })
                    .build()
            )
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        FunSpec.builder("create")
                            .addParameter(
                                "builder", LambdaTypeName.get(receiver = ClassName("", "Builder"), returnType = UNIT)
                            )
                            .build()
                    )
                    .build()
            )
            .addTypes(incoming.nestedTypes.map { TypeTransformer.transform(it) })
            .addProperties(incoming.declaredFields.mapIndexed { index, field ->
                PropertySpec.builder(field.name, parameterTypes[index])
                    .initializer(field.name)
                    .addAnnotation(Annotations.protoNumber(field.tag))
                    .build()
            })
            .build()
    }
}