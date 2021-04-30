package oscar.answers.controller.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oscar.answers.controller.BaseDoncallController;
import oscar.answers.service.CallCounter;
import oscar.answers.service.SpinLock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/doncall")
public class DoncallControllerV1 extends BaseDoncallController {

    @Value("${call_limit}")
    private int CALL_LIMIT;
    private final CallCounter callCounter;
    private Logger logger;
    private SpinLock spinLock;

    @Autowired
    public DoncallControllerV1(CallCounter callCounter) {
        super(callCounter);
        this.callCounter = callCounter;
        logger = LoggerFactory.getLogger(DoncallControllerV1.class);
        spinLock = new SpinLock();
    }

    @GetMapping("delicate")
    public ResponseEntity<?> donCallOneHundredPerMinute() {
        int minute = LocalDateTime.now().getMinute();
        String user = getClientIp() + ":" + minute;
        ResponseEntity<?> result;
        try {
            spinLock.lock();
            if (isAllowed(callCounter, user, CALL_LIMIT)) {
                // logger.info("Current request count: " + callCounter.getCountAndExpiretime(user));
                result = ResponseEntity
                        .ok()
                        .body("[CALL COUNT, COUNT EXPIRE TIME]: " + callCounter.getCountAndExpiretime(user));
            } else {
                // logger.info("Rejected. Available tokens: " + "?");
                result = ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("DO NOT CALL ME");
            }
        } finally {
            spinLock.unlock();
        }
        return result;
    }

    public int getCallLimit() {
        return CALL_LIMIT;
    }
}

