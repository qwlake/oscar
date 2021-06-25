package com.carrotins.boot.queue.controller

import com.carrotins.boot.queue.entity.TestEntity
import com.carrotins.boot.queue.service.TestEntityService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/test")
class TestController(
    val testEntityService: TestEntityService
) {

    @ApiOperation(value = "Test 생성")
    @PostMapping
    fun post(@RequestBody testEntity: TestEntity): ResponseEntity<*> {
        return if (testEntityService.isValid(testEntity)) {
            ResponseEntity.ok(testEntityService.create(testEntity))
        } else ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("")
    }
}