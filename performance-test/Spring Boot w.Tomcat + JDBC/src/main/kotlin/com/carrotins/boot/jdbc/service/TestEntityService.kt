package com.carrotins.boot.jdbc.service

import com.carrotins.boot.jdbc.entity.TestEntity
import com.carrotins.boot.jdbc.repository.TestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TestEntityService(
    val testRepository: TestRepository
) {

    fun isValid(testEntity: TestEntity): Boolean {
        return testEntity.name.isNotEmpty()
    }

    fun getAll(): Any {
        return testRepository.findAll()
    }

    fun getById(id: Long): Any {
        return testRepository.findById(id)
    }

    fun create(testEntity: TestEntity): Any {
        return testRepository.save(testEntity)
    }

    @Transactional
    fun update(testEntity: TestEntity): Any {
        return testRepository.findById(testEntity.id).ifPresent { t ->
            t.name = testEntity.name
            t.createdAt = testEntity.createdAt
            t.createdBy = testEntity.createdBy
            t.modifiedAt = testEntity.modifiedAt
            t.modifiedBy = testEntity.modifiedBy
            testRepository.save(t)
        }
    }

    @Transactional
    fun delete(id: Long): Any {
        return testRepository.deleteById(id)
    }
}