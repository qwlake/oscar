package com.carrotins.boot.r2dbc.repository

import com.carrotins.boot.r2dbc.entity.TestEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : R2dbcRepository<TestEntity, Long>