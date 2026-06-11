package chat.nami.kotlin.typeSystemAndLanguageCore

data class Person(val id: Int, val name: String)

fun main() {
    val user = Person(1, "Ömer")
    val (id, name) = user
    // compiles to:
    // val id = user.component1()
    // val name = user.component2()
    println("$id $name")

    // In lambdas — ONE destructured parameter vs TWO parameters:
    val map = mapOf(1 to "a", 2 to "b")
    map.forEach { (key, value) -> println("$key=$value") }   // one Entry, destructured
    map.mapValues { entry -> entry.value.uppercase() }       // one parameter, no destructuring
}
