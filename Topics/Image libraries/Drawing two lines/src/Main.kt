import java.awt.Color
import java.awt.image.BufferedImage  

fun drawLines(): BufferedImage {
  // Add your code here
    val myImage = BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB)
    val graphics = myImage.createGraphics()

    graphics.color = java.awt.Color.RED
    graphics.drawLine(0, 0, 200, 200)
    graphics.color = java.awt.Color.GREEN
    graphics.drawLine(0, 200, 200, 0)

    return myImage
}
