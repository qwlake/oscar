package oscar.kotlin.shortenUrl.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import oscar.kotlin.shortenUrl.domain.CountsEntity
import oscar.kotlin.shortenUrl.domain.CountsRepository
import oscar.kotlin.shortenUrl.dto.CountDto
import oscar.kotlin.shortenUrl.mapper.ShortenMapper
import oscar.kotlin.shortenUrl.service.ShortenConstant.SHORTEN_PREFIX
import java.util.*

@Component
class ScheduledTasks (
    val shortenMapper: ShortenMapper,
    val countsRepository: CountsRepository,
    val stringRedisTemplate: StringRedisTemplate
    ) {

    @Scheduled(fixedRate = 1000 * 60)
    fun flushRedisAndStoreCountsEveryHour() {
        val keys = stringRedisTemplate.keys("$SHORTEN_PREFIX*")
        val hashOperations = stringRedisTemplate.opsForHash<String, Int>()
        for (encoded in keys) {
            val entries = hashOperations.entries(encoded)
            val decoded = entries.keys.stream().findFirst().orElseThrow()
            val count = entries.values.stream().findFirst().orElse(0)
            val countsOptional: Optional<CountsEntity> = countsRepository.findByEncoded(encoded)
            if (countsOptional.isPresent) {
                val counts: CountsEntity = countsOptional.get()
                counts.count += count
                countsRepository.save(counts)
            } else {
                val dto = CountDto(encoded, count)
                val entity = shortenMapper.toCountsEntity(dto)
                countsRepository.save(entity)
            }
            hashOperations.increment(encoded, decoded, -count.toLong())
        }
    }
}
