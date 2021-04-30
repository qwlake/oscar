package oscar.kotlin.shortenUrl.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer

@Configuration
class RedisConfig {

    @Value("\${spring.redis.port}")
    var port = 0

    @Value("\${spring.redis.host}")
    var host: String? = null

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(connectionFactory!!)
        return redisTemplate
    }

    @Bean
    fun stringRedisTemplate(connectionFactory: RedisConnectionFactory?): StringRedisTemplate {
        val srt = StringRedisTemplate()
        srt.hashValueSerializer = GenericToStringSerializer(Int::class.java)
        srt.setConnectionFactory(connectionFactory!!)
        return srt
    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = host!!
        redisStandaloneConfiguration.port = port
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }
}