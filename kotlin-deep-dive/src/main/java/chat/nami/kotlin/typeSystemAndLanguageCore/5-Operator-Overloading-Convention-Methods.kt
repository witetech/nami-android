package chat.nami.kotlin.typeSystemAndLanguageCore

import java.math.BigDecimal

class Money(val amount: BigDecimal) : Comparable<Money> {
    operator fun plus(other: Money) = Money(amount + other.amount)
    override operator fun compareTo(other: Money) = amount.compareTo(other.amount)
    override fun toString() = "₺$amount"
}

class Router {
    operator fun invoke(path: String) = println("navigating to $path")
}

fun main() {
    val a = Money(BigDecimal("10.50"))
    val b = Money(BigDecimal("4.50"))
    println(a + b)                 // plus convention → ₺15.00
    println(a > b)                 // compareTo convention → true

    val router = Router()
    router("/home")                // invoke convention
}
