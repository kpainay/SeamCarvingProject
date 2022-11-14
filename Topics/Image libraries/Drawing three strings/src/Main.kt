import java.awt.Color
import java.awt.image.BufferedImage

fun drawStrings(): BufferedImage {
  // Add your code here
    val myImage = BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)
    val pen = myImage.createGraphics()
    val msg = "Hello, images!"
    pen.color = Color.RED
    pen.drawString(msg, 50, 50)
    pen.color = Color.GREEN
    pen.drawString(msg, 51, 51)
    pen.color = Color.BLUE
    pen.drawString(msg, 52, 52)

    pen.dispose()
    return myImage
}