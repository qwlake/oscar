package com.carrotins.boot.jdbc.entity

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name="test2")
class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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