package oscar.testcoroutine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestCoroutineApplication

fun main(args: Array<String>) {
    runApplication<TestCoroutineApplication>(*args)
}
