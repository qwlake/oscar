package com.carrotins.boot.r2dbc.service

import com.carrotins.boot.r2dbc.entity.TestEntity
import com.carrotins.boot.r2dbc.repository.TestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TestEntityService(
    val testRepository: TestRepository
) {

    fun isValid(testEntity: TestEntity): Boolean {
        return testEntity.name.isNotEmpty()
    }

    fun getAll(): MutableList<TestEntity>? {
        return testRepository.findAll().collectList().block()
    }

    fun getById(id: Long): TestEntity? {
        return testRepository.findById(id).block()
    }

    fun create(testEntity: TestEntity): TestEntity? {
        return testRepository.save(testEntity).block()
    }

    @Transactional
    fun update(testEntity: TestEntity): TestEntity? {
        return testRepository.findById(testEntity.id)
            .flatMap { t ->
                t.name = testEntity.name
                t.createdAt = testEntity.createdAt
                t.createdBy = testEntity.createdBy
                t.modifiedAt = testEntity.modifiedAt
                t.modifiedBy = testEntity.modifiedBy
                testRepository.save(t)
            }.block()
    }

    @Transactional
    fun delete(id: Long): Void? {
        return testRepository.deleteById(id).block()
    }
}