package com.carrotins.poc.kotlindl.controller

import com.carrotins.poc.kotlindl.dto.ImageDetectionResponse
import com.carrotins.poc.kotlindl.dto.ImageRecognitionResponse
import com.carrotins.poc.kotlindl.enumeration.Animal
import com.carrotins.poc.kotlindl.service.ImageRecognition
import com.carrotins.poc.kotlindl.service.ObjectDetection
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImageController(
    private val imageRecognition: ImageRecognition,
    private val objectDetection: ObjectDetection,
) {

    @PostMapping("recognize")
    fun recognizeObject(@RequestParam("image") multipartFile: MultipartFile,): ImageRecognitionResponse {
        if (multipartFile.isEmpty) throw Exception()

        val objectName = imageRecognition.recognize(multipartFile)
        return ImageRecognitionResponse(objectName)
    }

    @PostMapping("detect")
    fun detectObject(
        @RequestParam("image") multipartFile: MultipartFile,
        @RequestParam("animal_type") animalType: String,
    ): ImageDetectionResponse {
        if (multipartFile.isEmpty) throw Exception()
        val animal = Animal.fromCode(animalType)?: throw Exception()

        val isExist = objectDetection.detectAnimalFromImage(multipartFile, animal)
        return ImageDetectionResponse(isExist)
    }
}