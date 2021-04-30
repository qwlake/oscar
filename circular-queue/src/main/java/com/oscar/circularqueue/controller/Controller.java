package com.oscar.circularqueue.controller;

import com.oscar.circularqueue.service.QueueService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
public class Controller {

    private QueueService queueService;

    @PostMapping("/create-queue/{size}")
    public ResponseEntity<?> createQueue(@PathVariable int size) {
        return ResponseEntity
                .ok()
                .body(queueService.createQueue(size));
    }

    @PostMapping("/queue/{qid}/insert")
    public ResponseEntity<?> enqueue(@PathVariable int qid, @RequestParam String value, @RequestParam Optional<String> printOpt) {
        return ResponseEntity
                .ok()
                .body(queueService.enQueue(qid, value, printOpt));
    }

    @PostMapping("/queue/{qid}/pop")
    public ResponseEntity<?> dequeue(@PathVariable int qid) {
        return ResponseEntity
                .ok()
                .body(queueService.deQueue(qid));
    }

    @GetMapping("/queue/{qid}")
    public ResponseEntity<?> lookup(@PathVariable int qid) {
        return ResponseEntity
                .ok()
                .body(queueService.lookup(qid));
    }

    @GetMapping("/queue")
    public ResponseEntity<?> getQueues() {
        return ResponseEntity
                .ok()
                .body(queueService.getQueues());
    }
}
