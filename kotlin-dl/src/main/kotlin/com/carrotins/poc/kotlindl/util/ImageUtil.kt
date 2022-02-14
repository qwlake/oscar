package com.carrotins.poc.kotlindl.util

import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO


fun resize(inputStream: InputStream?, width: Int, height: Int): BufferedImage {
    val inputImage = ImageIO.read(inputStream)
    val outputImage = BufferedImage(width, height, inputImage.type)
    val graphics2D = outputImage.createGraphics()
    graphics2D.drawImage(inputImage, 0, 0, width, height, null)
    graphics2D.dispose()
    return outputImage
}
