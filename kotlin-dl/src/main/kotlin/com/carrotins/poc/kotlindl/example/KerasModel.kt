package com.carrotins.poc.kotlindl.example

import io.jhdf.HdfFile
import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.RMSProp
import org.jetbrains.kotlinx.dl.api.inference.keras.loadWeights
import org.jetbrains.kotlinx.dl.dataset.image.ImageConverter
import java.io.File


fun runWithKerasModel() {
    val modelConfig = File("model/keras/modelConfig.json")
    val weights = File("model/keras/weights.h5")
    val model = Sequential.loadModelConfiguration(modelConfig)
    val imageArray = ImageConverter.toNormalizedFloatArray(File("dataset/test1/1.jpg"))

    model.use {
        it.compile(RMSProp(), Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS, Metrics.ACCURACY)

        it.loadWeights(HdfFile(weights))

        val prediction = it.predict(imageArray)
        println("Predicted label is: $prediction. This corresponds to class {labelsMap[prediction]}.")
    }
}