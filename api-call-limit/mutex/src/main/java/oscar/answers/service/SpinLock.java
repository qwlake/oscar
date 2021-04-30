package oscar.answers.service;

import java.util.concurrent.locks.ReentrantLock;

public class SpinLock extends ReentrantLock {

    public SpinLock() {
        super();
    }

    public void lock() {
        while (super.isLocked()) {
            // Spinning ..
        }
        super.lock();
    }

    public void unlock() {
        super.unlock();
    }
}
