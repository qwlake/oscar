package oscar.answers.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CallCounter {

    private Logger logger;
    private AtomicInteger counts;
    private Long expireTime;

    @Autowired
    public CallCounter() {
        this.logger = LoggerFactory.getLogger(CallCounter.class);
        this.counts = new AtomicInteger();
        this.expireTime = System.currentTimeMillis();
    }

    @Scheduled(fixedRate = 1000)
    public void removeCountAfterOneMinute() {
        if (expireTime + 60000L <= System.currentTimeMillis()) {
            expireTime = System.currentTimeMillis();
            counts.set(0);
        }
    }

    public List<?> getCountAndExpiretime() {
        return List.of(counts, expireTime);
    }

    public int get() {
        return counts.incrementAndGet();
    }
}
