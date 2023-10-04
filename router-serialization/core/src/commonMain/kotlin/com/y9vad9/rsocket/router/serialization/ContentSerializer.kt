package com.y9vad9.rsocket.router.serialization

import io.ktor.utils.io.core.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public interface ContentSerializer {
    /**
     * Serializes the given [packet] into a specific type [T].
     *
     * @param kClass The [KClass] representing the type [T].
     * @param packet The [ByteReadPacket] to be serialized.
     * @throws SerializationException if an error occurs during serialization.
     */
    public fun <T> decode(kType: KType, packet: ByteReadPacket): T

    /**
     * Deserializes the given value of type T into a ByteReadPacket.
     *
     * @param kClass The class of the value to be deserialized.
     * @param value The value to be deserialized.
     * @return The deserialized value as a ByteReadPacket.
     */
    public fun <T : Any> encode(kType: KType, value: T): ByteReadPacket
}

/**
 * Reified version of [ContentSerializer.decode]. Uses the reified type [T] to automatically infer its KClass.
 *
 * @param packet The [ByteReadPacket] to be serialized.
 */
public inline fun <reified T : Any> ContentSerializer.decode(packet: ByteReadPacket): T {
    return decode(typeOf<T>(), packet)
}

/**
 * Reified version of [ContentSerializer.encode]. Uses the reified type [T] to automatically infer its KClass.
 *
 * @param value The value to be deserialized.
 * @return The deserialized value as a ByteReadPacket.
 */
public inline fun <reified T : Any> ContentSerializer.encode(value: T): ByteReadPacket {
    return encode(typeOf<T>(), value)
}