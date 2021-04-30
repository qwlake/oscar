package oscar.answers.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/doncall")
public class DoncallController {

    private Bucket bucket;
    private static final int DURATION_OF_MINUTES = 1;
    private static final int CAPACITY_PER_DURATION = 100;
    private static final Logger logger = LoggerFactory.getLogger(DoncallController.class);

    @Autowired
    public DoncallController() {
        Refill refill = Refill.intervally(CAPACITY_PER_DURATION, Duration.ofMinutes(DURATION_OF_MINUTES));
        // Refill refill = Refill.greedy(1, Duration.ofSeconds(DURATION_OF_MINUTES));
        Bandwidth bandwidth = Bandwidth.classic(CAPACITY_PER_DURATION, refill);
        bucket =  Bucket4j.builder()
                .addLimit(bandwidth)
                .build();
    }

    @GetMapping("delicate")
    public ResponseEntity<?> donCallOneHundredPerMinute() {
        if (bucket.tryConsume(1)) {
            logger.info("Current request count: " + (CAPACITY_PER_DURATION - bucket.getAvailableTokens()));
            return ResponseEntity
                    .ok()
                    .body("YOU CAN CALL ME");
        } else {
            logger.info("Rejected. Available tokens: " + bucket.getAvailableTokens());
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("DO NOT CALL ME");
        }
    }
}
