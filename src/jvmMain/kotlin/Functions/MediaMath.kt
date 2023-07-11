package Functions

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun tsToTime(timestamp: Long): String {
    val totalSeconds = timestamp / 1000

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun saveImageToDesktop(image: BufferedImage, filename: String) {
    val outputDir = System.getProperty("user.home") + "/Desktop"
    val outputFile = File(outputDir, "$filename.png")

    try {
        ImageIO.write(image, "png", outputFile)
        println("Image saved successfully: ${outputFile.absolutePath}")
    } catch (e: Exception) {
        println("Failed to save image: ${e.message}")
    }
}