package com.carrotins.webflux.r2dbc.entity

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("test2")
class TestEntity {
    @Id
    var id: Long = 0

    var name: String = ""

    @field:CreatedDate
    var createdAt: LocalDateTime? = null

    @field:CreatedBy
    var createdBy: String? = null

    @field:LastModifiedDate
    var modifiedAt: LocalDateTime? = null

    @field:LastModifiedBy
    var modifiedBy: String? = null
}