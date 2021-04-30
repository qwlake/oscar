package com.oscar.circularqueue.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class MyQueue<E> {

    private List<E> queue;
    private int front, back;
    private int size;
    private int maxSize;

    public MyQueue(int size) {
        queue = new ArrayList<E>(size);
        front = 0;
        back = 0;
        this.size = 0;
        this.maxSize = size;
    }

    @Override
    public String toString() {
        int idx = front;
        StringBuffer sb = new StringBuffer();
        for (int i = front; i < front+size; i++) {
            sb.append(queue.get(i%maxSize));
            sb.append(" ");
        }
        return sb.toString();
    }

    public boolean isFull() {
        return size == maxSize;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void enqueue(E e) {
        if (isFull()) {
            throw new IllegalStateException();
        }

        if (back < maxSize) {
            queue.add(e);
        } else {
            queue.set(back % maxSize, e);
        }
        back++;
        size++;
    }

    public E dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        size--;
        return queue.get(front++ % maxSize);
    }
}
