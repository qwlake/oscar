package oscar.testtransaction.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UrlsRepository: JpaRepository<UrlsEntity, Long> {
}