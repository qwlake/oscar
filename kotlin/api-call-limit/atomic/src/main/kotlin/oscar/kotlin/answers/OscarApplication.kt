package oscar.kotlin.answers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class OscarApplication

fun main(args: Array<String>) {
    runApplication<OscarApplication>(*args)
}