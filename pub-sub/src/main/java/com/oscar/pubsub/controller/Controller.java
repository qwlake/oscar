package com.oscar.pubsub.controller;

import com.oscar.pubsub.service.RedisPublisher;
import com.oscar.pubsub.service.RedisSubscriber;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class Controller {

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
}
