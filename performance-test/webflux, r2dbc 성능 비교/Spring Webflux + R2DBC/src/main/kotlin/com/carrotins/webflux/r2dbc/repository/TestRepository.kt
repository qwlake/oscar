package com.carrotins.webflux.r2dbc.repository

import com.carrotins.webflux.r2dbc.entity.TestEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface TestRepository : ReactiveCrudRepository<TestEntity, Long> {
    fun save(testEntity: Mono<TestEntity>): Mono<Mono<TestEntity>>
}