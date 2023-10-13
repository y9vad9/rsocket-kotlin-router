package com.y9vad9.rsocket.router.serialization.json

import com.y9vad9.rsocket.router.serialization.ContentSerializer
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.serializer
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KType

/**
 * A content serializer that uses Cbor format for encoding and decoding data.
 *
 * @param cbor The Cbor object to use for serialization and deserialization.
 */
@ExperimentalSerializationApi
public class CborContentSerializer(private val cbor: Cbor = Cbor) : ContentSerializer {
    public companion object Default : ContentSerializer by CborContentSerializer()

    /**
     * Decodes a serialized object from a ByteReadPacket using Cbor serialization.
     *
     * @param kType The KType representing the type of the object to be decoded.
     * @param packet The ByteReadPacket containing the serialized object data.
     * @return The deserialized object of type T.
     */
    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalSerializationApi::class)
    override fun <T> decode(kType: KType, packet: ByteReadPacket): T {
        return cbor.decodeFromByteArray(
            (cbor.serializersModule.serializerOrNull(kType) ?: serializer()) as KSerializer<T>,
            packet.readBytes(),
        )
    }

    /**
     * Encodes the given value of type T to a ByteReadPacket using the Cbor serialization.
     *
     * @param kType The Kotlin type of the value.
     * @param value The value to encode.
     * @return The encoded value as a ByteReadPacket.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> encode(kType: KType, value: T): ByteReadPacket {
        return ByteReadPacket(
            cbor.encodeToByteArray(
                (cbor.serializersModule.serializerOrNull(kType) ?: serializer()) as KSerializer<T>,
                value,
            )
        )
    }
}