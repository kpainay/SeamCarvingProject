import java.awt.Color
import java.awt.image.BufferedImage  

fun drawCircles(): BufferedImage {
  // Add your code here
    val myImage = BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB)
    val graphics = myImage.createGraphics()

    graphics.color = java.awt.Color.RED
    graphics.drawOval(50, 50, 100, 100)
    graphics.color = java.awt.Color.YELLOW
    graphics.drawOval(50, 75, 100, 100)
    graphics.color = java.awt.Color.GREEN
    graphics.drawOval(75, 50, 100, 100)
    graphics.color = java.awt.Color.BLUE
    graphics.drawOval(75, 75, 100, 100)

    graphics.dispose()
    return myImage

}