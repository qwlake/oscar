package com.carrotins.boot.jdbc.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Aspect
@Component
class Aspect {
    @Around("execution(* com.carrotins.boot.jdbc.controller..*(..)) ")
    fun executionAspect(joinPoint: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch(joinPoint.target.toString())
        stopWatch.start()
        val result: Any = joinPoint.proceed()
        stopWatch.stop()
        println(stopWatch.prettyPrint())
        return result
    }
}