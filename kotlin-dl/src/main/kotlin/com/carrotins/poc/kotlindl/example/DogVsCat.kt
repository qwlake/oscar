package com.carrotins.poc.kotlindl.example

import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.WritingMode
import org.jetbrains.kotlinx.dl.api.core.activation.Activations
import org.jetbrains.kotlinx.dl.api.core.layer.convolutional.Conv2D
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input
import org.jetbrains.kotlinx.dl.api.core.layer.normalization.BatchNorm
import org.jetbrains.kotlinx.dl.api.core.layer.pooling.MaxPool2D
import org.jetbrains.kotlinx.dl.api.core.layer.regularization.Dropout
import org.jetbrains.kotlinx.dl.api.core.layer.reshaping.Flatten
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.RMSProp
import org.jetbrains.kotlinx.dl.api.inference.TensorFlowInferenceModel
import org.jetbrains.kotlinx.dl.dataset.OnFlyImageDataset
import org.jetbrains.kotlinx.dl.dataset.image.ColorOrder
import org.jetbrains.kotlinx.dl.dataset.preprocessor.*
import org.jetbrains.kotlinx.dl.dataset.preprocessor.generator.EmptyLabels
import org.jetbrains.kotlinx.dl.dataset.preprocessor.generator.FromFolders
import org.jetbrains.kotlinx.dl.dataset.preprocessor.image.InterpolationType
import org.jetbrains.kotlinx.dl.dataset.preprocessor.image.crop
import org.jetbrains.kotlinx.dl.dataset.preprocessor.image.resize
import org.jetbrains.kotlinx.dl.dataset.preprocessor.image.rotate
import java.io.File
import java.nio.file.Paths

val IMAGE_WIDTH = 128L
val IMAGE_HEIGHT = 128L
val IMAGE_CHANNELS = 3L
val MODEL_PATH = "model/dogVsCat"

private val model = Sequential.of(
    Input(
        IMAGE_WIDTH,
        IMAGE_HEIGHT,
        IMAGE_CHANNELS,
    ),
    Conv2D(
        filters = 32,
        kernelSize = longArrayOf(3, 3),
        activation = Activations.Relu,
    ),
    BatchNorm(axis = listOf(3)),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
    ),
    Dropout(keepProbability = 0.25F),

    Conv2D(
        filters = 64,
        kernelSize = longArrayOf(3, 3),
        activation = Activations.Relu,
    ),
    BatchNorm(axis = listOf(3)),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
    ),
    Dropout(keepProbability = 0.25F),

    Conv2D(
        filters = 128,
        kernelSize = longArrayOf(3, 3),
        activation = Activations.Relu,
    ),
    BatchNorm(axis = listOf(3)),
    MaxPool2D(
        poolSize = intArrayOf(1, 2, 2, 1),
    ),
    Dropout(keepProbability = 0.25F),

    Flatten(),
    Dense(
        outputSize = 512,
        activation = Activations.Relu,
    ),
    BatchNorm(axis = listOf(1)),
    Dropout(keepProbability = 0.5F),
    Dense(
        outputSize = 2,
        activation = Activations.Softmax,
    ),
)

private fun getDogCatDataset(type: String): OnFlyImageDataset {
    val imageDirectory = Paths.get("dataset/$type").toFile()

    val label = if (type == "train") {
        FromFolders(mapOf("cat" to 0, "dog" to 1))
    } else {
        EmptyLabels()
    }
    val preprocessing: Preprocessing = preprocess {
        transformImage {
            load {
                pathToData = imageDirectory
                imageShape = ImageShape(IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_CHANNELS)
                colorMode = ColorOrder.RGB
                labelGenerator = label
            }
        }
        transformImage {
            crop {
                left = 12
                right = 12
                top = 12
                bottom = 12
            }
            rotate {
                degrees = 15f
            }
            resize {
                outputWidth = 128
                outputHeight = 128
                interpolation = InterpolationType.NEAREST
            }
        }
        transformTensor {
            rescale {
                scalingCoefficient = 255f
            }
        }
    }

    return OnFlyImageDataset.create(preprocessing)
}

fun fit() {
    model.use {
        it.compile(loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS, optimizer = RMSProp(), metric = Metrics.ACCURACY)

        it.summary()

        it.fit(dataset = getDogCatDataset("train"), batchSize = 15, epochs = 3)
        it.save(File("model/dogVsCat"), writingMode = WritingMode.OVERRIDE)

        val accuracy = it.evaluate(dataset = getDogCatDataset("test1"), batchSize = 15).metrics[Metrics.ACCURACY]

        println("Accuracy: $accuracy")
    }
}

fun predict() {
    TensorFlowInferenceModel.load(File(MODEL_PATH)).use {
        val data = getDogCatDataset("test1")
        val prediction = it.predict(data.getX(0))

        println("prediction: $prediction")
    }
}
