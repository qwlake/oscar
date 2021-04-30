package com.oscar.shortenurl.service;

import com.oscar.shortenurl.domain.Counts;
import com.oscar.shortenurl.domain.CountsRepository;
import com.oscar.shortenurl.domain.Urls;
import com.oscar.shortenurl.domain.UrlsRepository;
import com.oscar.shortenurl.dto.UrlSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShortenService {

    private final UrlsRepository urlsRepository;
    private final CountsRepository countsRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final StringEncodeService stringEncodeService;

    public static final String SHORTEN_PREFIX = "shorten:";

    @Value("${config.host}")
    private String host;

    @Transactional
    public String saveShortenUrl(String originalUrl) throws Exception {
        try {
            UrlSaveRequestDto dto = new UrlSaveRequestDto(originalUrl);
            Urls savedUrl = urlsRepository.save(dto.toEntity());
            String encoded = stringEncodeService.encode(savedUrl.getId().toString());
            return "http://" + host+ "/" + encoded;
        } catch (Exception e) {
            throw new Exception("Please retry");
        }
    }

    private String verifyHostAndGetSubPath(String shortenUrl) throws Exception {
        URI uri = URI.create(shortenUrl);
        if (!uri.getHost().equals(host))
            throw new Exception("Please enter valid Shorten Url");
        return uri.getPath().substring(1);
    }

    private long getId(String path) {
        String decoded = stringEncodeService.decode(path);
        return Long.parseLong(decoded.substring(0, decoded.indexOf("/")));
    }

    private String getUrl(String path) {
        return urlsRepository
                .findById(getId(path))
                .map(Urls::getUrl)
                .orElseThrow(() -> new ExpressionException("Unknown shorten url."));
    }

    private void setPathAndIncreasePathCountToRedis(String encodedUrl, String plainUrl) {
        HashOperations<String, String, Integer> hashOperations = stringRedisTemplate.opsForHash();
        hashOperations.increment(SHORTEN_PREFIX + encodedUrl, plainUrl, 1);
    }

    public String getOriginalUrl(String encoded, boolean isFullPath) throws Exception {
        if (isFullPath)
            encoded = verifyHostAndGetSubPath(encoded);
        HashOperations<String, String, Integer> hashOperations = stringRedisTemplate.opsForHash();
        Set<String> keys = hashOperations.entries(SHORTEN_PREFIX + encoded).keySet();

        String plainUrl = keys.stream().findFirst().orElse(getUrl(encoded));
        setPathAndIncreasePathCountToRedis(encoded, plainUrl);
        return plainUrl;
    }

    public int getRedirectCount(String path) {
        return countsRepository.findByPath(path).map(Counts::getCount).orElse(-1);
    }
}
