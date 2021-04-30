package oscar.answers.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisRateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimiter.class);
    private static final int REQUESTS_PER_MINUTE = 100;
    private final StringRedisTemplate stringTemplate;

    @Autowired
    public RedisRateLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringTemplate = stringRedisTemplate;
    }

    public boolean isAllowed(String username) {
        final int minute = LocalDateTime.now().getMinute();
        String key = username + ":" + minute;
        ValueOperations<String, String> operations = stringTemplate.opsForValue();
        String requests = operations.get(key);
        if (StringUtils.isNotBlank(requests) && Integer.parseInt(requests) >= REQUESTS_PER_MINUTE) {
            logger.info("Rejected. Available tokens: 0");
            return false;
        }
        List<Object> txResults = stringTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
                final StringRedisTemplate redisTemplate = (StringRedisTemplate) operations;
                final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
                operations.multi(); // 트랜젝션의 시작
                valueOperations.increment(key); // key에 대해 접근 count increment. key가 없을 경우 새로 생성되고 값도 1로 초기화된다.
                if (StringUtils.isBlank(requests)) // key가 처음 등록될 때에만 만료시간이 지정된다.
                    redisTemplate.expire(key, 1, TimeUnit.MINUTES); // 만료시간 지정 (1분 후 expire)
                return operations.exec(); // 트랜젝션의 종료
            }
        });
        logger.info("Current request count: " + txResults.get(0));
        return true;
    }
}