package com.example.test1.service

import org.springframework.stereotype.Service
import java.util.*


@Service
class TestService {

    fun bar(): String {
        val threadId = Thread.currentThread().id
        println(threadId.toString() + "start")

        // 실패
//        val currentTimeMillis = System.currentTimeMillis()
//        while(System.currentTimeMillis() < currentTimeMillis + 2000) {
//            // 2초가 경과할 때까지 대기
//        }

        // 실패
//        var time = Calendar.getInstance()
//        val timeInMillis = time.timeInMillis
//        while(time.timeInMillis < timeInMillis + 2000) {
//            // 2초가 경과할 때까지 대기
//            time = Calendar.getInstance()
//        }

        Thread.sleep(2000)

        println(threadId.toString() + "end")

        return "bar"
    }
}