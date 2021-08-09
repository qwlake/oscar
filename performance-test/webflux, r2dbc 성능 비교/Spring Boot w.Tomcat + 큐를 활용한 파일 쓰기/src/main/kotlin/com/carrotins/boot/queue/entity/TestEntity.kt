package com.carrotins.boot.queue.entity

import java.time.LocalDateTime


class TestEntity {
    var id: Long = 0

    var name: String = ""

    var createdAt: LocalDateTime? = LocalDateTime.now()

    var createdBy: String? = null

    var modifiedAt: LocalDateTime? = LocalDateTime.now()

    var modifiedBy: String? = null
}