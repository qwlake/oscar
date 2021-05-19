package oscar.testtransaction

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestTransactionApplication

fun main(args: Array<String>) {
    runApplication<TestTransactionApplication>(*args)
}
