package chat.nami.kotlin.functionsAndFunctionalStyle

data class Account(val id: Int, val name: String, val email: String)

fun validate(account: Account) {
    // closure: captures `account`
    fun requireField(value: String, field: String) {
        require(value.isNotEmpty()) { "$field empty for account ${account.id}" }
    }

    requireField(account.name, "name")
    requireField(account.email, "email")
}

// compiled to a loop — no stack frames
// tells the compiler to rewrite the recursion as a plain while loop in bytecode. No new stack frame per call.
tailrec fun gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)

fun main() {
    validate(Account(1, "Ömer", "omer@wite.com.tr"))
    println(gcd(48, 18))   // 6
}
