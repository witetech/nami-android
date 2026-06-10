package chat.nami.kotlin

open class User(val name: String)

class FreeUser(name: String) : User(name)
class PaidUser(name: String) : User(name)

class UserFeed<out T>(private val users: List<T>) {
    fun getAll(): List<T> = users
}

fun showUsers(feed: UserFeed<User>) {
    for (user in feed.getAll()) {
        println(user.name)
    }
}

interface UserProcessor<in T> {
    fun process(user: T)
}

class AnyUserProcessor : UserProcessor<User> {
    override fun process(user: User) {
        println("Processing user: ${user.name}")
    }
}

fun processFreeUser(user: FreeUser, processor: UserProcessor<FreeUser>) {
    processor.process(user)
}

fun processPaidUser(user: PaidUser, processor: UserProcessor<PaidUser>) {
    processor.process(user)
}

val anyUserProcessor: UserProcessor<User> = AnyUserProcessor()

val freeUsersFeed: UserFeed<FreeUser> = UserFeed(listOf(FreeUser(name = "Ömer")))
val paidUsersFeed: UserFeed<PaidUser> = UserFeed(listOf(PaidUser(name = "Faruk")))

// PECS: Producer-Extends, Consumer-Super
// from is projected: you can read Any, you cannot write into it
fun copyInto(from: Array<out Any>, to: Array<Any>) {
    for (i in from.indices) to[i] = from[i]
}

// Star projection
fun printAll(list: List<*>) {           // == List<out Any?>
    list.forEach { println(it) }        // reads as Any?
}

// Definitely Non-Nullable Type
fun <T> elvis(x: T, default: T & Any): T & Any = x ?: default

fun main() {
    showUsers(freeUsersFeed)
    showUsers(paidUsersFeed)

    processFreeUser(freeUsersFeed.getAll()[0], anyUserProcessor)
    processPaidUser(paidUsersFeed.getAll()[0], anyUserProcessor)

    val source: Array<String> = arrayOf("a", "b")
    val target: Array<Any> = arrayOf(1, 2)
    copyInto(source, target)
    println(target.toList())   // [a, b]

    printAll(listOf(1, "two", 3.0)) // [1, two, 3.0]

    val result: String = elvis(null, "fallback")
    println(result)   // fallback
}


