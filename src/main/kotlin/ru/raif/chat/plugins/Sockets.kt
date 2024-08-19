package ru.raif.chat.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import ru.raif.chat.cache.CacheService
import ru.raif.chat.model.ChatMessage
import java.time.Duration
import java.util.*

fun Application.configureSockets(cacheService: CacheService) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val sessions = Collections.synchronizedSet<WebSocketServerSession>(HashSet())

        webSocket("/chat") { // websocketSession
            sessions.add(this)
            cacheService.getAllMessages().forEach { message -> sendSerialized(message) }

            while (true) {
                val message = receiveDeserialized<ChatMessage>()
                cacheService.putMessageToCache(message)
                sessions.forEach {s -> s.sendSerialized(message)}
            }
        }

    }

}
