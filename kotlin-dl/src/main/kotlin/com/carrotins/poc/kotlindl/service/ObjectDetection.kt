package com.carrotins.poc.kotlindl.service

import com.carrotins.poc.kotlindl.enumeration.Animal
import org.jetbrains.kotlinx.dl.api.inference.loaders.ONNXModelHub
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject
import org.jetbrains.kotlinx.dl.api.inference.onnx.ONNXModels
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class ObjectDetection {

    companion object {
        // todo. need to check thread-safe
        val modelHub = ONNXModelHub(cacheDirectory = File("cache/pretrainedModels"))
        val model = ONNXModels.ObjectDetection.SSD.pretrainedModel(modelHub)
        private val logger = LoggerFactory.getLogger(ObjectDetection::class.java)
    }

    fun detectAnimalFromImage(file: MultipartFile, animal: Animal): Boolean {
        val fileName = file.originalFilename
        val fileBytes = file.bytes
        val imageFile = File("tmp/$fileName")

        try {
            imageFile.writeBytes(fileBytes)
        } catch (e: Exception) {
            return false
        }

        val detectedObjects = model.detectObjects(imageFile = imageFile, topK = 20)

        detectedObjects.forEach {
            logger.debug("Found ${it.classLabel} with probability ${it.probability}")
        }

        return findAnimal(detectedObjects, animal)
    }

    private fun findAnimal(detectedObjects: List<DetectedObject>, animal: Animal): Boolean {
        for (obj in detectedObjects) {
            if (obj.classLabel == animal.code && obj.probability >= 0.8) {
                return true
            }
            if (obj.probability < 0.8) {
                break
            }
        }
        return false
    }
}