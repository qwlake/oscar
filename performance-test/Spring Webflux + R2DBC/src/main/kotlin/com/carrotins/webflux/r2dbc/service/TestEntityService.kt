package com.carrotins.webflux.r2dbc.service

import com.carrotins.webflux.r2dbc.entity.TestEntity
import com.carrotins.webflux.r2dbc.repository.TestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class TestEntityService(
    val testRepository: TestRepository
) {

    fun isValid(testEntity: TestEntity): Boolean {
        return testEntity.name.isNotEmpty()
    }

    fun getAll(): Flux<*> {
        return testRepository.findAll()
    }

    fun getById(id: Long): Mono<*> {
        return testRepository.findById(id)
    }

    fun create(testEntity: TestEntity): Mono<*> {
        return testRepository.save(testEntity)
    }

    @Transactional
    fun update(testEntity: TestEntity): Mono<*> {
        return testRepository.findById(testEntity.id)
            .flatMap { t ->
                t.name = testEntity.name
                t.createdAt = testEntity.createdAt
                t.createdBy = testEntity.createdBy
                t.modifiedAt = testEntity.modifiedAt
                t.modifiedBy = testEntity.modifiedBy
                testRepository.save(t)
            }
    }

    @Transactional
    fun delete(id: Long): Mono<*> {
        return testRepository.findById(id)
            .flatMap(testRepository::delete)
    }
}