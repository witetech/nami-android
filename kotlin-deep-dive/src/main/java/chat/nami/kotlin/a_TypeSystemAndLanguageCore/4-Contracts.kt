package chat.nami.kotlin.a_TypeSystemAndLanguageCore

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun requireNotBlank(value: String?): String {
    contract { returns() implies (value != null) }
    if (value.isNullOrBlank()) throw IllegalArgumentException("blank")
    return value
}

fun main() {
    val s: String? = "hello"
    requireNotBlank(s)
    println(s.length)   // smart cast — compiler trusts the contract

    val x: Int
    run {        // stdlib contract: callsInPlace(block, EXACTLY_ONCE)
        x = 42   // legal because compiler knows the block runs exactly once
    }
    println(x)
}
