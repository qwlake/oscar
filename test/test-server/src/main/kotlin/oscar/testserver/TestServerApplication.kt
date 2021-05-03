package oscar.testserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
class TestServerApplication

fun main(args: Array<String>) {
    runApplication<TestServerApplication>(*args)
}
