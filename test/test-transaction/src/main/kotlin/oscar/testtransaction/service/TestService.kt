package oscar.testtransaction.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import oscar.testtransaction.domain.UrlsEntity
import oscar.testtransaction.domain.UrlsRepository

@Service
class TestService(val urlsRepository: UrlsRepository) {

    @Transactional
    fun get(): MutableList<UrlsEntity> {
        return urlsRepository.findAll()
    }

    @Transactional
    fun save(): UrlsEntity {
        val urlsEntity = UrlsEntity()
        urlsEntity.url = "test"
        urlsRepository.save(urlsEntity)
        return urlsEntity
    }

    @Transactional
    fun tran(): List<UrlsEntity> {
        val urlsEntity1 = UrlsEntity()
        val urlsEntity2 = UrlsEntity()
        urlsRepository.save(urlsEntity1)
        Assert.isTrue(false, "test1")
        urlsRepository.save(urlsEntity2)
        return listOf(urlsEntity1, urlsEntity2)
    }
}