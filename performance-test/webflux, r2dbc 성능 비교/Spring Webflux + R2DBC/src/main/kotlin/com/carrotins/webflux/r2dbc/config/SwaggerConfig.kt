package com.carrotins.webflux.r2dbc.config

import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.util.*

@Profile("local", "predev", "dev")
@Configuration
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation::class.java))
            .build()
            .genericModelSubstitutes(Optional::class.java, Flux::class.java, Mono::class.java)
    }
}
