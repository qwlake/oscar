package com.carrotins.boot.queue.service

import com.carrotins.boot.queue.entity.TestEntity
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue
import javax.annotation.PostConstruct


@Service
class TestEntityService(
    val fileService: FileService
) {

    private val queueCapacity = 10000
    val queueContainer: ConcurrentLinkedQueue<LinkedBlockingQueue<TestEntity>> = ConcurrentLinkedQueue()

    @PostConstruct
    fun init() {
        queueContainer.add(LinkedBlockingQueue<TestEntity>(queueCapacity))
    }

    fun isValid(testEntity: TestEntity): Boolean {
        return testEntity.name.isNotEmpty()
    }

    fun create(testEntity: TestEntity): String {
        synchronized(queueContainer) {
            if (queueContainer.peek().size == queueCapacity) {
                val polledQueue = queueContainer.poll()
                Thread {
                    fileService.writeStream(polledQueue.stream())
                }.start()
                queueContainer.add(LinkedBlockingQueue<TestEntity>(queueCapacity))
            }
            queueContainer.peek().add(testEntity)
        }
        return "success"
    }
}