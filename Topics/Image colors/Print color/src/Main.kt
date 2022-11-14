
fun printColor(myImage: BufferedImage) {

    var color = Color(myImage.getRGB(0, 0))
    val colorCoordinates = readln().split(" ").map { it.toInt() }
    color = Color(myImage.getRGB(colorCoordinates[0], colorCoordinates[1]), true)

    println("${color.red} ${color.green} ${color.blue} ${color.alpha}")
}
