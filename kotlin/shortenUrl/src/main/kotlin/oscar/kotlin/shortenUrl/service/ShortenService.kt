package oscar.kotlin.shortenUrl.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.expression.ExpressionException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import oscar.kotlin.shortenUrl.domain.CountsRepository
import oscar.kotlin.shortenUrl.domain.UrlsRepository
import oscar.kotlin.shortenUrl.dto.RedirectCountDto
import oscar.kotlin.shortenUrl.dto.UrlDto
import oscar.kotlin.shortenUrl.mapper.ShortenMapper
import oscar.kotlin.shortenUrl.service.ShortenConstant.SHORTEN_PREFIX

@Service
class ShortenService (
    private val shortenMapper: ShortenMapper,
    private val urlsRepository: UrlsRepository,
    private val countsRepository: CountsRepository,
    private val stringRedisTemplate: StringRedisTemplate,
    private val stringEncodeService: StringEncodeService
    ) {

    @Value("\${config.host}")
    private var host: String = ""

    @Transactional
    fun saveShortenUrl(urlDto: UrlDto): UrlDto {
        val urlsEntity = shortenMapper.toUrlsEntity(urlDto)
        urlsRepository.save(urlsEntity)
        val encoded = stringEncodeService.encode(urlsEntity.id.toString())
        return UrlDto(encoded)
    }

    fun getOriginalUrl(encoded: String): String {
        val hashOperations: HashOperations<String, String, Int> = stringRedisTemplate.opsForHash()
        val keys: Set<String> = hashOperations.entries(SHORTEN_PREFIX + encoded).keys
        val plainUrl: String = keys.stream().findFirst().orElseGet{getUrl(encoded)}
        setPathAndIncreasePathCountToRedis(encoded, plainUrl)
        return plainUrl
    }

    fun getRedirectCount(encoded: String): RedirectCountDto {
        val count = countsRepository
            .findByEncoded(encoded)
            .map { counts -> counts.count }
            .orElse(0)
        return RedirectCountDto(count)
    }

    private fun getUrl(encoded: String): String {
        return urlsRepository
            .findById(getId(encoded))
            .map { url -> url.url }
            .orElseThrow { ExpressionException("Unknown shorten url.") }
    }

    private fun getId(encoded: String): Long {
        val decoded: String = stringEncodeService.decode(encoded)
        return decoded.substring(0, decoded.indexOf("/")).toLong()
    }

    private fun setPathAndIncreasePathCountToRedis(encodedUrl: String, plainUrl: String) {
        val hashOperations: HashOperations<String, String, Int> = stringRedisTemplate.opsForHash()
        hashOperations.increment(SHORTEN_PREFIX + encodedUrl, plainUrl, 1)
    }
}