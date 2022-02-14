package com.carrotins.poc.kotlindl.service

import com.carrotins.poc.kotlindl.util.resize
import org.jetbrains.kotlinx.dl.api.inference.keras.loaders.TFModelHub
import org.jetbrains.kotlinx.dl.api.inference.keras.loaders.TFModels
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

@Service
class ImageRecognition {

    companion object {
        // todo. need to check thread-safe
        private val modelHub = TFModelHub(cacheDirectory = File("cache/pretrainedModels"))
        private val model = modelHub[TFModels.CV.MobileNetV2]
        private const val imageSize = 224
        private val logger = LoggerFactory.getLogger(ImageRecognition::class.java)
    }

    fun recognize(file: MultipartFile): String {
        val fileName = file.originalFilename
        val bufferedImage = resize(file.inputStream, imageSize, imageSize)
        val imageFile = File("tmp/$fileName")
        val stream = ByteArrayOutputStream()

        try {
            ImageIO.write(bufferedImage, "jpg", stream)
            stream.flush()
            imageFile.writeBytes(stream.toByteArray())
        } catch (e: Exception) {
            return "fail"
        } finally {
            stream.close()
        }

        val recognizedObject = model.predictObject(imageFile = imageFile)
        val topK = model.predictTopKObjects(imageFile = imageFile, topK = 5)

        logger.debug(topK.toString())

        return recognizedObject
    }
}