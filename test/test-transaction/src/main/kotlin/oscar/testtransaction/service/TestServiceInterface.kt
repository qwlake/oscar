package oscar.testtransaction.service

import oscar.testtransaction.domain.UrlsEntity

interface TestServiceInterface {

    fun tran(): List<UrlsEntity>
}