package com.y9vad9.rsocket.router.serialization.json

import com.y9vad9.rsocket.router.serialization.ContentSerializer
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KType

/**
 * A content serializer that uses JSON format for encoding and decoding data.
 *
 * @property json The JSON object to use for serialization and deserialization.
 */
public class JsonContentSerializer(private val json: Json = Json) : ContentSerializer {
    /**
     * Decodes a serialized object from a ByteReadPacket using JSON serialization.
     *
     * @param kType The KType representing the type of the object to be decoded.
     * @param packet The ByteReadPacket containing the serialized object data.
     * @return The deserialized object of type T.
     */
    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalSerializationApi::class)
    override fun <T> decode(kType: KType, packet: ByteReadPacket): T {
        return json.decodeFromStream(
            (json.serializersModule.serializerOrNull(kType) ?: serializer()) as KSerializer<T>,
            packet.asStream()
        )
    }

    /**
     * Encodes the given value of type T to a ByteReadPacket using the JSON serialization.
     *
     * @param kType The Kotlin type of the value.
     * @param value The value to encode.
     * @return The encoded value as a ByteReadPacket.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> encode(kType: KType, value: T): ByteReadPacket {
        return ByteReadPacket(
            json.encodeToString(
                (json.serializersModule.serializerOrNull(kType) ?: serializer()) as KSerializer<T>,
                value,
            ).toByteArray()
        )
    }
}