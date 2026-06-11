package chat.nami.kotlin.equalityImmutabilityState

data class LineItem(val sku: String)
data class Order(val id: Int, val items: MutableList<LineItem>)

// Encapsulation leak — copy() stays public even with a private constructor.
// @ExposedCopyVisibility keeps copy public (old behavior, silences the 2.0.20+ warning);
// @ConsistentCopyVisibility makes copy match the constructor visibility instead.
@ExposedCopyVisibility
data class Email private constructor(val value: String) {
    companion object {
        fun of(s: String): Email? = if ("@" in s) Email(s) else null
    }
}

fun main() {
    val a = Order(1, mutableListOf(LineItem("EV-CABLE")))
    val b = a.copy(id = 2)
    b.items.add(LineItem("ADAPTER"))
    println(a.items)   // [EV-CABLE, ADAPTER] — a mutated too — SHALLOW copy

    val valid = Email.of("a@b.c")!!
    val invalid = valid.copy(value = "not-an-email")   // bypassed factory validation
    println(invalid)
}

// Mitigations: keep only immutable types inside data classes (List, not MutableList);
// @ConsistentCopyVisibility; or use a normal class.
