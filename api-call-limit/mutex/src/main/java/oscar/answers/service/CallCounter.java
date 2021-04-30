package oscar.answers.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CallCounter {

    private Logger logger;
    private Map<String, List<?>> userCounts;

    @Autowired
    public CallCounter() {
        this.logger = LoggerFactory.getLogger(CallCounter.class);
        this.userCounts = new HashMap<>();
    }

    @Scheduled(fixedRate = 1000)
    public void removeCountAfterOneMinute() {
//        logger.info(String.valueOf(userCounts));
        userCounts.values().removeIf(
                value -> (long) value.get(1) + 60000L <= System.currentTimeMillis()
        );
    }

    public Map<String, List<?>> getUserCounts() {
        return userCounts;
    }

    public List<?> getCountAndExpiretime(String user) {
        return userCounts.get(user);
    }

    public Optional<List<?>> get(String user) {
        return Optional.ofNullable(userCounts.get(user));
    }

    public void put(String user, List<?> countAndExpiretime) {
        userCounts.put(user, countAndExpiretime);
    }

    public boolean containsKey(String user) {
        return userCounts.containsKey(user);
    }

    public void clear() {
        userCounts.clear();
    }
}
