package oscar.testjdbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestJdbcApplication

fun main(args: Array<String>) {
    runApplication<TestJdbcApplication>(*args)
}
