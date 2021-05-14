package oscar.testtransaction.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import oscar.testtransaction.domain.UrlsEntity
import oscar.testtransaction.service.TestService

@RestController
class TestController(@Autowired val testService: TestService) {

    @GetMapping("hello")
    fun hello(): String {
        return "hello"
    }

    @GetMapping("get")
    fun get(): MutableList<UrlsEntity> {
        return testService.get()
    }

    @GetMapping("save")
    fun save(): UrlsEntity {
        return testService.save()
    }

    @GetMapping("tran")
    fun tran(): List<UrlsEntity> {
        return testService.tran()
    }
}