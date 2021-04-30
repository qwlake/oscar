package oscar.kotlin.answers.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import oscar.kotlin.answers.service.CallCounter
import oscar.kotlin.answers.service.MyService

@RestController
@RequestMapping("doncall")
class DoncallController (
    val callCounter: CallCounter,
    val myService: MyService
    ) {

    @Value("\${call_limit}")
    private var CALL_LIMIT : Int = 0
    private val logger: Logger = LoggerFactory.getLogger(DoncallController::class.java)
//    private val mainScope = CoroutineScope(Dispatchers.Main)


    @GetMapping("delicate")
    fun donCallOneHundredPerMinute(): ResponseEntity<*> {
        var result: ResponseEntity<*>
        if (isAllowed(callCounter, CALL_LIMIT)) {
            result = ResponseEntity
                .ok()
                .body("[CALL COUNT, COUNT EXPIRE TIME]: " + callCounter.getCountAndExpiretime())
        } else {
            result = ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body("DO NOT CALL ME")
        }
        return result
    }

    @GetMapping("foo")
    suspend fun foo(): ResponseEntity<*> {
        val result = myService.foo()
        return ResponseEntity.ok(result)
    }


    fun isAllowed(callCounter: CallCounter, callLimit: Int): Boolean {
        return callCounter.get() <= callLimit
    }

    fun getCallLimit(): Int {
        return CALL_LIMIT
    }
}