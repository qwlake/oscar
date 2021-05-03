package oscar.testcoroutine.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import oscar.testcoroutine.service.TestService

@RestController
class TestController(val testService: TestService) {

    @GetMapping("foo")
    suspend fun foo(): ResponseEntity<*> {
        val result = testService.foo()
        return ResponseEntity.ok(result)
    }
}