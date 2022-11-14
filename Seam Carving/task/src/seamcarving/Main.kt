package seamcarving
import kotlin.math.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
const val MYTESTFILE = "C:\\Users\\lawre\\OneDrive\\Desktop\\blue.png"
const val MYTESTOUT = "C:\\Users\\lawre\\OneDrive\\Desktop\\blueOut.png"
var SEAMSUM = 0.0
fun main(args: Array<String>) {

    val reduceWidthBy =  if (args.size > 5) args[5].toInt() else 50
    val reduceHeightBy =  if(args.size >6) args[7].toInt() else 50


    val imageFile = if (args.isEmpty()) MYTESTFILE else args[1]
    val outFile = if (args.isEmpty()) MYTESTOUT else args[3]
    val image = ImageIO.read(File(imageFile))
    val ref = ImageIO.read(File(imageFile))
    val engine = SeamCarvingEngine(image)
    var newImage = image

    // reduce width
    // cut vertical seams
    repeat(reduceWidthBy) {
        val seam = engine.findVerticalSeam(newImage)
        newImage = engine.removeSeam(seam, newImage)
    }

    // reduce height (transpose image)
    // cut horizontal seams
    var img = engine.transposeImage(newImage)
    repeat(reduceHeightBy) {
        val seam = engine.findHorizontalSeam(img)
        img = engine.removeSeam(seam, img)
    }

    // transpose back
    val finalImage = engine.transposeImage(img)

    ImageIO.write(finalImage, "png", File(outFile))

}

class SeamCarvingEngine(var img: BufferedImage) {
    var image: BufferedImage? = null
    fun negateImage(img: BufferedImage): BufferedImage {
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                val color = Color(img.getRGB(x, y))
                val nred = 255 - color.red
                val ngreen = 255 - color.green
                val nblue = 255 - color.blue
                img.setRGB(x, y, Color(nred, ngreen, nblue).rgb)
            }
        }
        return img
    }
    fun getEnergy(img: BufferedImage): Map<Pair<Int, Int>, Double> {
        val energies = mutableMapOf<Pair<Int, Int>, Double>()
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                var a = x
                var b = y
                val xGradSquared = getXGradiantSquared(img, Pair(when(x){
                                                                        0 -> 1
                                                                        img.width-1 -> img.width-2
                                                                        else -> x
                                                                        }, y))
                val yGradSquared = getYGradiantSquared(img, Pair(x, when (y) {
                                                                        0 -> 1
                                                                        img.height-1 -> img.height-2
                                                                        else -> y
                                                                        }))
                val pixelEnergy = sqrt(xGradSquared.toDouble() + yGradSquared)

                energies[Pair(x, y)] = pixelEnergy
            }
        }
        return energies
    }

    fun renderGreyScale(img: BufferedImage): BufferedImage {
        val pixelEnergies = getEnergy(img)
        var mxEnergy = 0.0
        for ((key, value ) in pixelEnergies) {
            if (value > mxEnergy) mxEnergy = value
        }
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                val pixelEnergy = pixelEnergies[Pair(x, y)]
                val intensity = ((255.0 * pixelEnergy!!) / mxEnergy).toInt()
                val color = Color(intensity, intensity, intensity)
                img.setRGB(x, y, color.rgb)
            }
        }
        return img
    }

    private fun getGradientsSquared(img: BufferedImage, coordinates: Pair<Int, Int>): Pair<Double, Double> {
        var x = coordinates.first
        var y = coordinates.second

        // get the Colors of the surrounding pixels
        if (x==0) x=1
        if (y==0) y=1
        if (x==img.width-1) x=img.width-2
        if (y==img.height-1) y=img.height-2
        val xColorPrev = Color(img.getRGB(x - 1, y))
        val xColorNext = Color(img.getRGB(x + 1, y))

        val yColorPrev = Color(img.getRGB(x, y - 1))
        val yColorNext = Color(img.getRGB(x, y + 1))
        // x gradient
        val xRed = (xColorPrev.red - xColorNext.red).toDouble().pow(2.0)
        val xGreen = (xColorPrev.green - xColorNext.green).toDouble().pow(2.0)
        val xBlue = (xColorPrev.blue - xColorNext.blue).toDouble().pow(2.0)
        val xGradSquared = (xRed + xBlue + xGreen)
        // y gradient
        val yRed = (yColorPrev.red - yColorNext.red).toDouble().pow(2.0)
        val yGreen = (yColorPrev.green - yColorNext.green).toDouble().pow(2.0)
        val yBlue = (yColorPrev.blue - yColorNext.blue).toDouble().pow(2.0)
        val yGradSquared = (yRed + yBlue + yGreen)

        return Pair(xGradSquared, yGradSquared)
    }
    private fun getXGradiantSquared(img: BufferedImage, coordinates: Pair<Int, Int>): Int {
        val x = coordinates.first
        val y = coordinates.second

        val xColorPrev = Color(img.getRGB(x - 1, y))
        val xColorNext = Color(img.getRGB(x + 1, y))

        val xRed = (xColorPrev.red - xColorNext.red)
        val xGreen = (xColorPrev.green - xColorNext.green)
        val xBlue = (xColorPrev.blue - xColorNext.blue)

        return (xRed * xRed + xBlue * xBlue + xGreen * xGreen)
    }
    private fun getYGradiantSquared(img: BufferedImage, coordinates: Pair<Int, Int>): Int {
        val x = coordinates.first
        val y = coordinates.second

        val xColorPrev = Color(img.getRGB(x, y - 1))
        val xColorNext = Color(img.getRGB(x, y + 1))

        val xRed = (xColorPrev.red - xColorNext.red)
        val xGreen = (xColorPrev.green - xColorNext.green)
        val xBlue = (xColorPrev.blue - xColorNext.blue)

        return (xRed * xRed + xBlue * xBlue + xGreen * xGreen)

    }
    fun drawLine(img: BufferedImage, start: Pair<Int, Int>, end: Pair<Int, Int>): BufferedImage {
        val pen = img.createGraphics()
        pen.color = Color.RED
        pen.drawLine(start.first, start.second, end.first, end.second)
        pen.dispose()
        return img

    }

    fun getSeam() = findHorizontalSeam(img)

    // highlight the seam
    fun colorSeam(img: BufferedImage, seam: List<Pair<Int, Int>>) {
        for (pixel in seam) {
            img.setRGB(pixel.first, pixel.second, Color.RED.rgb)
        }
    }

    fun transposeImage(img: BufferedImage): BufferedImage {
        val newImg = BufferedImage(img.height, img.width, img.type)
        for (i in 0 until img.height) {
            for (j in 0 until img.width) {
                newImg.setRGB(i, j, img.getRGB(j, i))
            }
        }
        return newImg
    }

    fun findHorizontalSeam(img: BufferedImage): List<Pair<Int, Int>> {
        return MyGraph.Energy.findSeam(img)
    }
    fun findVerticalSeam(img: BufferedImage): List<Pair<Int, Int>> {
        return MyGraph.Energy.findSeam(img)
    }

    fun removeSeam(seam: List<Pair<Int, Int>>, img: BufferedImage): BufferedImage {
        val newImage = BufferedImage(img.width-1, img.height, img.type)
        for (y in img.height - 1 downTo 0){
            var x = 0
            for (xOld in 0 until img.width) {
                if (seam.contains(Pair(xOld, y))) continue else newImage.setRGB(x, y, img.getRGB(xOld, y))
                x++
            }
        }
        return newImage
    }


    fun restoreColor(ref: BufferedImage, img: BufferedImage): BufferedImage {
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                img.setRGB(x, y, ref.getRGB(x, y))
            }
        }
        return img
    }
}
class Pixel(
    var x: Int,
    var y: Int,
    var energy: Double
)


class MyGraph(private val img: BufferedImage) {

    private val seam = mutableListOf<Pixel>()
    val energyMap = Array(img.width) { Array(img.height) { 0.0 } }
    var bestPixel = Pixel(0, 0, Double.MAX_VALUE)

    object Energy {

        private fun energy(gradientX: Int, gradientY: Int): Double {
            return sqrt(gradientX.toDouble() + gradientY)
        }

        fun delta(color: Color, diffColor: Color): Int {
            val redDiff = color.red - diffColor.red
            val greenDiff = color.green - diffColor.green
            val blueDiff = color.blue - diffColor.blue
            return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff
        }

        fun getImageEnergy(image: BufferedImage):Map<Pair<Int, Int>, Double> {
            val width = image.width
            val height = image.height
            val energy : MutableMap<Pair<Int, Int>, Double> = mutableMapOf()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val rgbHighX = Color(
                        image.getRGB(
                            when (x) {
                                0 -> 2
                                width - 1 -> x
                                else -> x + 1
                            }, y
                        )
                    )
                    val rgbLowX = Color(
                        image.getRGB(
                            when (x) {
                                0 -> 0
                                width - 1 -> x - 2
                                else -> x - 1
                            }, y
                        )
                    )
                    val rgbHighY = Color(
                        image.getRGB(
                            x,
                            when (y) {
                                0 -> 2
                                height - 1 -> y
                                else -> y + 1
                            }
                        )
                    )
                    val rgbLowY = Color(
                        image.getRGB(
                            x,
                            when (y) {
                                0 -> 0
                                height - 1 -> y - 2
                                else -> y - 1
                            }
                        )
                    )
                    val gradientX = delta(rgbHighX, rgbLowX)
                    val gradientY = delta(rgbHighY, rgbLowY)
                    energy[Pair(x, y,)] = this.energy(gradientX, gradientY)
                }
            }
            return energy
        }
        private fun transpose(matrix: Array<Array<Double>>): Array<Array<Double>>{
            val newMatrix = Array(matrix[0].size){Array(matrix.size){0.0} }
            for (i in newMatrix.indices) {
                for (j in matrix.indices) {
                    newMatrix[i][j] = matrix[j][i]
                }
            }
            return newMatrix
        }

        fun findSeam(InsideImage: BufferedImage): List<Pair<Int, Int>> {
            val image: BufferedImage = BufferedImage(InsideImage.width, InsideImage.height, InsideImage.type)
            for (x in 0 until InsideImage.width) {
                for (y in 0 until InsideImage.height) {
                    image.setRGB(x, y, InsideImage.getRGB(x, y))
                }
            }


            val energies = Array(image.width) { Array(image.height) { 0.0 } }

            // fill energy matrix
            for (w in 0 until image.width) {
                for (h in 0 until image.height) {
                    energies[w][h] = calculateEnergy(image, w, h)
                }
            }
            // what is this? why is it needed?
            val maxEnergy = energies.map { it.maxOrNull()!! }.maxOrNull()!! // max energy in the image

            // replace each image pixel with normalized energy value (grey -> red, green, blue are all same value)
            for (w in 0 until image.width) {
                for (h in 0 until image.height) {
                    val energy = energies[w][h]
                    val intensity = (255.0 * energy / maxEnergy).toInt()
                    val alpha = Color(image.getRGB(w, h)).alpha
                    image.setRGB(w, h, Color(intensity, intensity, intensity, alpha).rgb)
                }
            }

            return findMinSumPathInEnergyMatrix(energies, image)
        }

        private fun findMinSumPathInEnergyMatrix(energies: Array<Array<Double>>, image: BufferedImage): List<Pair<Int, Int>> {
            val width = image.width
            val height = image.height

            val sumMatrix = Array(width) { Array(height) { 0.0 } }

            // fill sumMatrix from top to bottom
            // each cell is sum of its energy and minimum energy of three neighbors in row above (up left, left, up right)
            // https://en.m.wikipedia.org/wiki/Seam_carving#Dynamic_programming
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (y == 0) {
                        // top row has no row above it, so will be initialized with energy data
                        sumMatrix[x][y] = energies[x][y]
                    } else {
                        val neighbors = getUpstairNeighbors(sumMatrix, x, y)
                        sumMatrix[x][y] = energies[x][y] + minOf(Double.MAX_VALUE, *neighbors)
                    }
                }
            }

            val seam = emptyList<Pair<Int, Int>>().toMutableList()

            // find minimum in last row and ...
            var minimumEnergy = Double.MAX_VALUE
            var xOfMinimum = 0
            for (x in 0 until width) {
                if (sumMatrix[x][height - 1] < minimumEnergy) {
                    minimumEnergy = sumMatrix[x][height - 1]
                    xOfMinimum = x
                }
            }
            seam.add(Pair(xOfMinimum, height - 1))

            // ... step back up each row, only up+left, up, up+right, always choose the minimum
            for (y in height - 1 downTo 0) {
                val neighbors = getUpstairNeighbors(sumMatrix, xOfMinimum, y + 1)

                val min = minOf(Double.MAX_VALUE, *neighbors)

                xOfMinimum += when (min) {
                    neighbors[0] -> -1 // minimum was up left
                    neighbors[2] -> +1 // minimum was up right
                    else -> 0 // minimum was directly above
                }

                seam.add(Pair(xOfMinimum, y))
            }

            return seam
        }

        // get energy of three fields above field (w/h)
        // returns array with three values of top left, top, top right
        private fun getUpstairNeighbors(sumMatrix: Array<Array<Double>>, x: Int, y: Int): Array<Double> {
            val rightBorder = sumMatrix.size
            return arrayOf(
                if (x == 0) Double.MAX_VALUE else sumMatrix[x - 1][y - 1],
                sumMatrix[x][y - 1],
                if (x == rightBorder - 1) Double.MAX_VALUE else sumMatrix[x + 1][y - 1]
            )
        }

        private fun calculateEnergy(image: BufferedImage, x: Int, y: Int): Double {
            val posX = when (x) {
                0 -> 1
                image.width - 1 -> image.width - 2
                else -> x
            }
            val posY = when (y) {
                0 -> 1
                image.height - 1 -> image.height - 2
                else -> y
            }

            val westPixel = Color(image.getRGB(posX - 1, y))
            val eastPixel = Color(image.getRGB(posX + 1, y))
            val xDiffSquared = diffSquared(westPixel, eastPixel)

            val northPixel = Color(image.getRGB(x, posY - 1))
            val southPixel = Color(image.getRGB(x, posY + 1))
            val yDiffSquared = diffSquared(northPixel, southPixel)

            return sqrt(xDiffSquared + yDiffSquared)
        }

        private fun diffSquared(c1: Color, c2: Color): Double {
            val red = c1.red - c2.red
            val green = c1.green - c2.green
            val blue = c1.blue - c2.blue
            return (red * red + green * green + blue * blue).toDouble()
        }
    }


}

