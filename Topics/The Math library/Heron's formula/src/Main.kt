import kotlin.math.*

fun main() {
    // put your code here
    val sideA = readln().toInt()
    val sideB = readln().toInt()
    val sideC = readln().toInt()

    val semiPeri = (sideA + sideB + sideC) / 2.0
    val area = sqrt(semiPeri * (semiPeri - sideA) * (semiPeri - sideB) * (semiPeri - sideC))
    println(area)
}