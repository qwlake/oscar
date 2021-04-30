package com.oscar.circularqueue.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@NoArgsConstructor
public class QueueService<E> {

    private Map<Integer, MyQueue<E>> queueMap;
    private int queueId;

    @PostConstruct
    public void init() {
        queueMap = new HashMap<>();
        queueId = 1000;
    }

    public int createQueue(int size) {
        try {
            MyQueue<E> queue = new MyQueue<E>(size);
            queueMap.put(queueId, queue);
            return queueId++;
        } catch (Exception e) {
            return -1;
        }
    }

    public String enQueue(int qid, E value, Optional<String> printOpt) {
        try {
            queueMap.get(qid)
                    .enqueue(value);
            if (printOpt.isPresent() && printOpt.get().equals("true")) {
                return queueMap.get(qid).toString();
            } else {
                return "Insert Success";
            }
        } catch (IllegalStateException | NullPointerException e) {
            return "Error, " + e;
        }
    }

    public String deQueue(int qid) {
        try {
            return queueMap.get(qid)
                    .dequeue()
                    .toString();
        } catch (NoSuchElementException | NullPointerException e) {
            return "Error, " + e;
        }
    }

    public String lookup(int qid) {
        try {
            return queueMap.get(qid).toString();
        } catch (NoSuchElementException | NullPointerException e) {
            return "Error, " + e;
        }
    }

    public Set<Integer> getQueues() {
        return queueMap.keySet();
    }
}
