package com.oscar.shortenurl.service;

import com.oscar.shortenurl.domain.Counts;
import com.oscar.shortenurl.domain.CountsRepository;
import com.oscar.shortenurl.dto.CountSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final CountsRepository countsRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Scheduled(fixedRate = 1000 * 60)
    public void flushRedisAndStoreCountsEveryHour() {

        Set<String> keys = stringRedisTemplate.keys(ShortenService.SHORTEN_PREFIX + "*");
        HashOperations<String, String, Integer> hashOperations = stringRedisTemplate.opsForHash();

        for (String encoded: keys) {
            Map<String, Integer> entries = hashOperations.entries(encoded);
            String decoded = entries.keySet().stream().findFirst().orElseThrow();
            int count = entries.values().stream().findFirst().orElse(0);
            Optional<Counts> countsOptional = countsRepository.findByPath(encoded);
            if (countsOptional.isPresent()) {
                Counts counts = countsOptional.get();
                counts.setCount(counts.getCount() + count);
                countsRepository.save(counts);
            } else {
                CountSaveRequestDto dto = new CountSaveRequestDto(encoded, count);
                countsRepository.save(dto.toEntity());
            }
            hashOperations.increment(encoded, decoded, -count);
        }
    }
}
