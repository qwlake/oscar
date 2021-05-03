package oscar.testserver.controller

import oscar.testserver.service.TestService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Callable


@Controller
class TestController(val testService: TestService) {

    @GetMapping("index")
    fun index(): Callable<String> {
//        val bar = testService.bar()
//        return ResponseEntity.ok(bar)
        return Callable<String> { testService.bar() }
    }
}