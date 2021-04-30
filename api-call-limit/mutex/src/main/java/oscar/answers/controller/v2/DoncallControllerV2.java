package oscar.answers.controller.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oscar.answers.controller.BaseDoncallController;
import oscar.answers.service.CallCounter;

import java.time.LocalDateTime;

@RestController
@RequestMapping("v2/doncall")
public class DoncallControllerV2 extends BaseDoncallController {

    @Value("${call_limit}")
    private int CALL_LIMIT;
    private final CallCounter callCounter;
    private Logger logger;

    public DoncallControllerV2(CallCounter callCounter) {
        super(callCounter);
        this.callCounter = callCounter;
        logger = LoggerFactory.getLogger(DoncallControllerV2.class);
    }

    @GetMapping("delicate")
    public ResponseEntity<?> donCallOneHundredPerMinute() {
        int minute = LocalDateTime.now().getMinute();
        String user = getClientIp() + ":" + minute;
        ResponseEntity<?> result;
        synchronized(this) {
            if (isAllowed(callCounter, user, CALL_LIMIT)) {
                // logger.info("Current request count: " + callCounter.getCountAndExpiretime(user));
                result = ResponseEntity
                        .ok()
                        .body("[CALL COUNT, COUNT EXPIRE TIME]: " + callCounter.getCountAndExpiretime(user));
            } else {
                // logger.info("Rejected.");
                result = ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("DO NOT CALL ME");
            }
        }
        return result;
    }

    public int getCallLimit() {
        return CALL_LIMIT;
    }
}
