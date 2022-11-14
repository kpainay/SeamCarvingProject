
fun main() {
    val input = readlnOrNull() // you need to add something
    val result = input ?: throw IllegalStateException()
    println("Elvis says: $result")

}