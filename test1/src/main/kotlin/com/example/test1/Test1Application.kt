package com.example.test1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
class Test1Application

fun main(args: Array<String>) {
    runApplication<Test1Application>(*args)
}
