package com.example.test1.controller

import com.example.test1.service.TestService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class TestController(val testService: TestService) {

    @GetMapping("index")
    fun index(): ResponseEntity<*> {
        val bar = testService.bar()
        return ResponseEntity.ok(bar)
    }
}