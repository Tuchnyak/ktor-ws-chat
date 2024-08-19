package ru.raif.chat.cache

import ru.raif.chat.model.ChatMessage
import java.util.concurrent.TimeUnit

/**
 * @author tuchnyak (George Shchennikov)
 */
interface CacheService {

    fun getAllMessages(): List<ChatMessage>
    fun putMessageToCache(message: ChatMessage)
    fun removeOldMessages(durationVaue: Long, timeUnit: TimeUnit): Int
    fun clearCache()
    fun getCacheSize(): Int

}