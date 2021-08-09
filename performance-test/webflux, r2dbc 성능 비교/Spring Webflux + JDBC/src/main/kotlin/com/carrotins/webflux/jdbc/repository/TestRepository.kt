package com.carrotins.webflux.jdbc.repository

import com.carrotins.webflux.jdbc.entity.TestEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : JpaRepository<TestEntity, Long>