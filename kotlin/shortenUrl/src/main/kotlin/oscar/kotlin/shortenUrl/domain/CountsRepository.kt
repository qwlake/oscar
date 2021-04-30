package oscar.kotlin.shortenUrl.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CountsRepository: JpaRepository<CountsEntity, Long> {
    fun findByEncoded(encoded: String): Optional<CountsEntity>
}