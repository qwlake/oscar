package com.carrotins.boot.queue.service

import com.carrotins.boot.queue.entity.TestEntity
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.stream.Stream


@Service
class FileService(
    val objectMapper: ObjectMapper
) {

    var buffer = ByteArray(8 * 1024)
    val outputStream: OutputStream = FileOutputStream("src/main/resources/file.txt", true)

    fun write(any: Any): String {
        val toWrite = objectMapper.writeValueAsString(any)
        outputStream.write(toWrite.toByteArray() + '\n'.code.toByte())
        return toWrite
    }

    fun writeStream(stream: Stream<*>) {
        var i = 1L
        for (s in stream) {
            val testEntity = s as TestEntity
            testEntity.id = i++
            val toWrite = objectMapper.writeValueAsBytes(testEntity)
            outputStream.write(toWrite + '\n'.code.toByte())
        }
        outputStream.flush()
    }
}