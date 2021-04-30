package oscar.answers.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oscar.answers.service.CallCounter;

import java.time.LocalDateTime;

@RestController
@RequestMapping("doncall")
public class DoncallController {

    @Value("${call_limit}")
    private int CALL_LIMIT;
    private final CallCounter callCounter;
    private Logger logger;

    public DoncallController(CallCounter callCounter) {
        this.callCounter = callCounter;
        logger = LoggerFactory.getLogger(DoncallController.class);
    }

    private Boolean isAllowed(CallCounter callCounter, int callLimit) {
        return callCounter.get() <= callLimit;
    }

    @GetMapping("delicate")
    public ResponseEntity<?> donCallOneHundredPerMinute() {
        int minute = LocalDateTime.now().getMinute();
        ResponseEntity<?> result;
        if (isAllowed(callCounter, CALL_LIMIT)) {
            // logger.info("Current request count: " + callCounter.getCountAndExpiretime());
            result = ResponseEntity
                    .ok()
                    .body("[CALL COUNT, COUNT EXPIRE TIME]: " + callCounter.getCountAndExpiretime());
        } else {
            // logger.info("Rejected.");
            result = ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("DO NOT CALL ME");
        }
        return result;
    }

    public int getCallLimit() {
        return CALL_LIMIT;
    }
}
