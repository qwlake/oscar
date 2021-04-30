package com.oscar.shortenurl.service;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class StringEncodeService {
    private final Base62 base62;
    private final Random random;

    public StringEncodeService() {
        base62 = Base62.createInstance();
        random = new Random();
    }

    public String encode(String plain) {
        return new String(base62.encode((plain + "/" + getFixedRandomSalt()).getBytes()));
    }

    private String getFixedRandomSalt() {
        return Integer.toString(1000 + random.nextInt(9000));
    }

    public String decode(String encode) {
        return new String(base62.decode(encode.getBytes()));
    }
}
