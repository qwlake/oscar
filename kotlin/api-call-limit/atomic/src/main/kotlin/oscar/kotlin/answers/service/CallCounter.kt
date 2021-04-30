package oscar.kotlin.answers.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class CallCounter {

    private val logger: Logger = LoggerFactory.getLogger(CallCounter::class.java)
    private val counts: AtomicInteger = AtomicInteger()
    private var expireTime: Long = System.currentTimeMillis()

    @Scheduled(fixedRate = 1000)
    fun removeCountAfterOneMinute() {
        if (expireTime + 60000L <= System.currentTimeMillis()) {
            expireTime = System.currentTimeMillis()
            counts.set(0)
        }
    }

    fun getCountAndExpiretime(): List<*> {
        return listOf(counts, expireTime)
    }

    fun get(): Int {
        return counts.incrementAndGet()
    }
}