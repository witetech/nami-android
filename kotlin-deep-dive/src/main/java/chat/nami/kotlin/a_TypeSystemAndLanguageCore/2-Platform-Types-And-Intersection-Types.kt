package chat.nami.kotlin.a_TypeSystemAndLanguageCore

import chat.nami.kotlin.JavaUser

fun <T> firstIfBigger(a: T, b: T): T where T : CharSequence, T : Comparable<T> {
    // inside, T is effectively CharSequence & Comparable<T>
    return if (a > b) a else b
}

fun main() {
    val javaUser = JavaUser()

    // String! (platform type)
    val name = javaUser.name

    // String? (platform type)
    val surname: String? = javaUser.surname

    // NPE possible at runtime, compiler stays silent
    println(name.length)

    // explicit type at the boundary
    val safe: String? = javaUser.name
    println(safe?.length)

    println(firstIfBigger("banana", "apple"))   // banana
}
