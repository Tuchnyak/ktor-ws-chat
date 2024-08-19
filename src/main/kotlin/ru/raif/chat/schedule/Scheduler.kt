package ru.raif.chat.schedule

import io.ktor.server.application.*
import io.ktor.util.logging.*
import ru.raif.chat.cache.CacheService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

internal val LOGGER = KtorSimpleLogger("Scheduler")

fun Application.configureScheduler(cacheService: CacheService) {
    val cacheExpirationSeconds = environment.config.propertyOrNull("chat.cache.expiration.seconds")?.getString()?.toLongOrNull() ?: 60L

    log.info(">>> Configure scheduler with expiration value: {}", cacheExpirationSeconds)
    SchedulerHolder.initScheduler(cacheService, cacheExpirationSeconds)
    log.info(">>> Scheduler has been configured")

    SchedulerHolder.startScheduler()
    log.info(">>> Scheduler has been started")
}

private object SchedulerHolder {
    private var scheduler: Scheduler? = null

    fun initScheduler(cacheService: CacheService, cacheExpirationSeconds: Long) {
        scheduler = Scheduler.build(cacheService, cacheExpirationSeconds)
    }

    fun startScheduler() {
        scheduler?.schedule()
    }
}

data class Scheduler internal constructor(
    val cacheService: CacheService,
    val cacheExpirationSeconds: Long,
    val timeUnit: TimeUnit,
    val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
) {
    companion object
}

fun Scheduler.Companion.build(cacheService: CacheService, cacheExpirationSeconds: Long) = Scheduler(
    cacheService,
    cacheExpirationSeconds,
    TimeUnit.SECONDS
)

fun Scheduler.schedule(): Unit = with(Scheduler) {
    executorService.scheduleWithFixedDelay(
        {
            if (cacheService.getCacheSize() > 0) {
                LOGGER.info(">>> Remove from cache messages older than {} ({})", cacheExpirationSeconds, timeUnit)
                val removed = cacheService.removeOldMessages(cacheExpirationSeconds, timeUnit)
                LOGGER.info(">>> '{}' have been removed from cache", removed)
            }
        },
        cacheExpirationSeconds,
        cacheExpirationSeconds,
        timeUnit
    )
}