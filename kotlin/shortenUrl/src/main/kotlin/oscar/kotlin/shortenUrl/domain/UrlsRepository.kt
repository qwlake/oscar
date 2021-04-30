package oscar.kotlin.shortenUrl.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UrlsRepository: JpaRepository<UrlsEntity, Long> {
}