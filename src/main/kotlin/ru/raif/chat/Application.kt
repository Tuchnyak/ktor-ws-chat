package ru.raif.chat

import io.ktor.server.application.*
import ru.raif.chat.cache.CacheService
import ru.raif.chat.cache.CacheServiceInMemoryImpl
import ru.raif.chat.plugins.configureRouting
import ru.raif.chat.plugins.configureSerialization
import ru.raif.chat.plugins.configureSockets
import ru.raif.chat.schedule.configureScheduler

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    val cacheService: CacheService = CacheServiceInMemoryImpl()

    configureSerialization()
    configureSockets(cacheService)
    configureRouting()
    configureScheduler(cacheService)
}

