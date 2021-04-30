package oscar.kotlin.shortenUrl

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan

@ComponentScan("oscar.kotlin.shortenUrl")
class ServletInitializer : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(ShortenUrlApplication::class.java)
    }

}
