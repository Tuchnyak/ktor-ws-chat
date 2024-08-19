package ru.raif.chat.cache

import ru.raif.chat.model.ChatMessage
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class CacheServiceInMemoryImpl : CacheService {

    private val storage: ConcurrentHashMap<ChatMessage, Instant> = ConcurrentHashMap()

    override fun getAllMessages(): List<ChatMessage> {

        return storage.map { it.key }.toList()
    }

    override fun putMessageToCache(message: ChatMessage) {
        storage[message] = message.timestamp
    }

    override fun removeOldMessages(durationVaue: Long, timeUnit: TimeUnit): Int {
        if (storage.size == 0) return 0;

        val expireTime = Instant.now().minus(durationVaue, timeUnit.toChronoUnit())
        val filteredList = storage.filter { it.value.isBefore(expireTime) }
            .map { it.key }
            .toList()
        filteredList.forEach { storage.remove(it) }

        return filteredList.size
    }

    override fun clearCache() {
        storage.clear()
    }

    override fun getCacheSize(): Int {

        return storage.size
    }

}