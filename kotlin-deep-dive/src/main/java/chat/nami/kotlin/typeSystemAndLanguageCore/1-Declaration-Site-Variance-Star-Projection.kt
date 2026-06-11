package chat.nami.kotlin.typeSystemAndLanguageCore

// Declaration-site variance — declared on the class:

// out T - T is returned, produced, read from
// in T  - T is accepted, consumed, written into
// T     - Both read and written, so invariant

private open class User(val name: String)

private class FreeUser(name: String) : User(name)
private class PaidUser(name: String) : User(name)

private class UserFeed<out T>(private val users: List<T>) {
    fun getAll(): List<T> = users
}

private fun showUsers(feed: UserFeed<User>) {
    for (user in feed.getAll()) {
        println(user.name)
    }
}

// =====================================================================

private interface UserProcessor<in T> {
    fun process(user: T)
}

private class AnyUserProcessor : UserProcessor<User> {
    override fun process(user: User) {
        println("Processing user: ${user.name}")
    }
}

private fun processFreeUser(user: FreeUser, processor: UserProcessor<FreeUser>) {
    processor.process(user)
}

private fun processPaidUser(user: PaidUser, processor: UserProcessor<PaidUser>) {
    processor.process(user)
}

// =====================================================================

// Star projection

fun printAll(list: List<*>) {
    list.forEach { println(it) }
}

fun fillFirst(array: Array<*>) {
    // array[0] = "x" // compile error — type unknown
    println("size=${array.size}, first=${array.firstOrNull()}")
}

private val freeUsersFeed: UserFeed<FreeUser> = UserFeed(listOf(FreeUser(name = "Ömer")))
private val paidUsersFeed: UserFeed<PaidUser> = UserFeed(listOf(PaidUser(name = "Faruk")))

fun main() {
    showUsers(freeUsersFeed)
    showUsers(paidUsersFeed)

    val anyUserProcessor: UserProcessor<User> = AnyUserProcessor()
    processFreeUser(freeUsersFeed.getAll()[0], anyUserProcessor)
    processPaidUser(paidUsersFeed.getAll()[0], anyUserProcessor)

    printAll(listOf(1, "two", 3.0))
    fillFirst(arrayOf("a", "b"))
}
