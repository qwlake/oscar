package com.carrotins.boot.jdbc.controller

import com.carrotins.boot.jdbc.entity.TestEntity
import com.carrotins.boot.jdbc.service.TestEntityService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/test")
class TestController(
    val testEntityService: TestEntityService
) {
    @ApiOperation(value = "Test 전체 조회")
    @GetMapping
    fun get(): ResponseEntity<*> {
        return ResponseEntity.ok(testEntityService.getAll())
    }

    @ApiOperation(value = "Test 1개 조회")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<*> {
        return ResponseEntity.ok(testEntityService.getById(id))
    }

    @ApiOperation(value = "Test 생성")
    @PostMapping
    fun post(@RequestBody testEntity: TestEntity): ResponseEntity<*> {
        return if (testEntityService.isValid(testEntity)) {
            ResponseEntity.ok(testEntityService.create(testEntity))
        } else ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("")
    }

    @ApiOperation(value = "Test 수정")
    @PutMapping
    fun put(@RequestBody testEntity: TestEntity): ResponseEntity<*> {
        return if (testEntityService.isValid(testEntity)) {
            ResponseEntity.ok(testEntityService.update(testEntity))
        } else ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("")
    }

    @ApiOperation(value = "Test 삭제")
    @DeleteMapping
    fun delete(@RequestParam id: Long): ResponseEntity<*> {
        return if (id > 0) {
            ResponseEntity.ok(testEntityService.delete(id))
        } else ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("")
    }
}