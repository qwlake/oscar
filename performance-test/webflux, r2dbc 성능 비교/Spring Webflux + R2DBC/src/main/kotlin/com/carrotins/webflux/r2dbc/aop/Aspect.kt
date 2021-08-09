package com.carrotins.webflux.r2dbc.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Around
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch


@Aspect
@Component
class Aspect {
    @Around("execution(* com.carrotins.webflux.r2dbc.controller..*(..)) ")
    fun executionAspect(joinPoint: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch(joinPoint.target.toString())
        stopWatch.start()
        val result: Any = joinPoint.proceed()
        stopWatch.stop()
        println(stopWatch.prettyPrint())
        return result
    }
}