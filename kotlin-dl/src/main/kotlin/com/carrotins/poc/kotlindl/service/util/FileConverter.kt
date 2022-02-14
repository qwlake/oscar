package com.carrotins.poc.kotlindl.service.util

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Service
class FileConverter {

    companion object {
        private val logger = LoggerFactory.getLogger(FileConverter::class.java)
    }

    fun convertMultiPartToFile(multipartFile: MultipartFile): File {
        val result = File(multipartFile.originalFilename)
        try {
            FileOutputStream(result).use { fos -> fos.write(multipartFile.bytes) }
        } catch (e: IOException) {
            logger.error("Unable to convert MultiPart to File!", e)
        }
        return result
    }
}