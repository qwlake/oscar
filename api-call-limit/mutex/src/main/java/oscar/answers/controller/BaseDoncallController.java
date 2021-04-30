package oscar.answers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import oscar.answers.service.CallCounter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
public abstract class BaseDoncallController {

    private CallCounter callCounter;

    @Autowired
    public BaseDoncallController(CallCounter callCounter) {
        this.callCounter = callCounter;
    }

    public abstract ResponseEntity<?> donCallOneHundredPerMinute();

    public abstract int getCallLimit();

    protected Boolean isAllowed(CallCounter callCounter, String user, int callLimit) {
        if (!callCounter.containsKey(user)) {
            callCounter.put(user, List.of(1, System.currentTimeMillis()));
            return true;
        }
        boolean result = false;
        Optional<List<?>> optionalCount = callCounter.get(user);
        if (optionalCount.isPresent() && (int) optionalCount.get().get(0) < callLimit) {
            callCounter.put(user, List.of((int) optionalCount.get().get(0) + 1, optionalCount.get().get(1)));
            result = true;
        }
        return result;
    }

    protected String getClientIp() {
        String ip = "127.0.0.1";
        try {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            ip = req.getHeader("X-FORWARDED-FOR");
            if (ip == null)
                ip = req.getRemoteAddr();
        } catch (Exception ignored) {
        }
        return ip;
    }

    public void clearCounts() {
        callCounter.clear();
    }
}
