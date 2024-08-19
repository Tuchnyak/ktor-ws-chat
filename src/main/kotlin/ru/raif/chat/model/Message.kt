package ru.raif.chat.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

/**
 * @author tuchnyak (George Shchennikov)
 */
@Serializable
data class ChatMessage(
    val message: String,
    val userName: String,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant
)

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val decodedStringValue = decoder.decodeString()

        return Instant.parse(decodedStringValue)
    }

    override fun serialize(encoder: Encoder, value: Instant): Unit {
        encoder.encodeString(value.toString())
    }

}



