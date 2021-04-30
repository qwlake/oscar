package com.oscar.shortenurl.controller;

import com.oscar.shortenurl.service.ShortenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
public class ShortenController {

    private ShortenService shortenService;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> postShorten(@RequestBody Map<String, String> originalUrl) {
        try {
            String url = shortenService.saveShortenUrl(originalUrl.get("url"));
            return ResponseEntity
                    .ok()
                    .body(url);
        } catch (Exception e) {
            String error = "Unknown Error\n" + e;
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error); // 서버 에러를 반환하는 좋은 방법?
        }
    }

    @GetMapping("/redirect")
    public ResponseEntity<String> getRedirect(@RequestBody Map<String, String> shortenUrl) throws Exception {
        try {
            String url = shortenService.getOriginalUrl(shortenUrl.get("url"), true);
            return ResponseEntity
                    .status(HttpStatus.PERMANENT_REDIRECT)
                    .header("Location", url)
                    .build();
        } catch (Exception e) {
            String error = "Unknown Error\n" + e;
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error); // 서버 에러를 반환하는 좋은 방법?
        }
    }

    @GetMapping("/redirect/{path}")
    public ResponseEntity<String> getRedirect(@PathVariable String path) {
        try {
            String url = shortenService.getOriginalUrl(path, false);
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .header("Location", url)
                    .build();
        } catch (Exception e) {
            String error = "Unknown Error\n" + e;
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error); // 서버 에러를 반환하는 좋은 방법?
        }
    }

    @GetMapping("/redirect/{path}/count")
    public ResponseEntity<String> getRedirectCount(@PathVariable String path) {
        try {
            long count = shortenService.getRedirectCount(path);
            return ResponseEntity
                    .ok()
                    .body(path + " redirect count: " + count);
        } catch (Exception e) {
            String error = "Unknown Error\n" + e;
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(error); // 서버 에러를 반환하는 좋은 방법?
        }
    }
}
