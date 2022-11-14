import kotlin.math.*

fun main() {
    // write your code here
    val angle = readLine()!!.toDouble()
    val rad = angle
    val diffInSinCos = sin(rad) - cos(rad)
    println(diffInSinCos)
}