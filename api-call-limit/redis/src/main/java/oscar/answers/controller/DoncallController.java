package oscar.answers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import oscar.answers.service.RedisRateLimiter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/doncall")
public class DoncallController {

    private RedisRateLimiter redisRateLimiter;

    @Autowired
    public DoncallController(RedisRateLimiter redisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
    }

    /**
     * 확실하게 동작하지 않음.
     * req.getHeader("X-FORWARDED-FOR") -> null
     * req.getRemoteAddr() -> 0:0:0:0:0:0 (local 이라 그런가? 테스트 해봐야 함)
     * @return Client IP
     */
    private String getClientIp() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        return ip;
    }

    @GetMapping("delicate")
    public ResponseEntity<?> donCallOneHundredPerMinute() {
        String clientIp = getClientIp();
        if (redisRateLimiter.isAllowed(clientIp)) {
            return ResponseEntity
                    .ok()
                    .body("YOU CAN CALL ME");
        } else {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("DO NOT CALL ME");
        }
    }
}
