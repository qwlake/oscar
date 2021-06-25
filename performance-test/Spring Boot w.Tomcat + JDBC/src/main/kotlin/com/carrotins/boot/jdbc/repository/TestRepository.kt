package com.carrotins.boot.jdbc.repository

import com.carrotins.boot.jdbc.entity.TestEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : JpaRepository<TestEntity, Long>