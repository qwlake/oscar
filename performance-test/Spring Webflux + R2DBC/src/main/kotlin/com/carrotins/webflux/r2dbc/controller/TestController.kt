package com.carrotins.webflux.r2dbc.controller

import com.carrotins.webflux.r2dbc.entity.TestEntity
import com.carrotins.webflux.r2dbc.service.TestEntityService
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/test")
class TestController(
    val testEntityService: TestEntityService
) {
    @ApiOperation(value = "Test 전체 조회")
    @GetMapping
    fun get(): Flux<*> {
        return testEntityService.getAll()
    }

    @ApiOperation(value = "Test 1개 조회")
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Mono<*> {
        return testEntityService.getById(id)
    }

    @ApiOperation(value = "Test 생성")
    @PostMapping
    fun post(@RequestBody testEntity: TestEntity): ResponseEntity<Mono<*>> {
        return if (testEntityService.isValid(testEntity)) {
            ResponseEntity.ok(testEntityService.create(testEntity))
        } else ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build()
    }

    @ApiOperation(value = "Test 수정")
    @PutMapping
    fun put(@RequestBody testEntity: TestEntity): ResponseEntity<Mono<*>> {
        return if (testEntityService.isValid(testEntity)) {
            ResponseEntity.ok(testEntityService.update(testEntity))
        } else ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build()
    }

    @ApiOperation(value = "Test 삭제")
    @DeleteMapping
    fun delete(@RequestParam id: Long): ResponseEntity<Mono<*>> {
        return if (id > 0) {
            ResponseEntity.ok(testEntityService.delete(id))
        } else ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build()
    }
}