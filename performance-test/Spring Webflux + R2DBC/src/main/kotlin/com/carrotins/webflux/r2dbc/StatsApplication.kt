package com.carrotins.webflux.r2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class StatsApplication

fun main(args: Array<String>) {
	runApplication<StatsApplication>(*args)
}
