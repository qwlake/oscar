package com.carrotins.webflux.jdbc.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import java.util.*

@Configuration
class AuditorAwareConfig: AuditorAware<String> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of("anonymousUser")
    }
}
