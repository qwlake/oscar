package com.carrotins.boot.r2dbc.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import reactor.core.publisher.Mono

@Configuration
class AuditorAwareConfig: ReactiveAuditorAware<String> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun getCurrentAuditor(): Mono<String> {
        return Mono.just("anonymousUser")
    }
}
