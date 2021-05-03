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

@RestController
@RequestMapping("doncall")
class DoncallController (
    val callCounter: CallCounter
    ) {

    @Value("\${call_limit}")
    private var CALL_LIMIT : Int = 0
    private val logger: Logger = LoggerFactory.getLogger(DoncallController::class.java)


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

    fun isAllowed(callCounter: CallCounter, callLimit: Int): Boolean {
        return callCounter.get() <= callLimit
    }

    fun getCallLimit(): Int {
        return CALL_LIMIT
    }
}