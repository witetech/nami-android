package chat.nami.kotlin.f_Coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Core idea: every coroutine has a parent; a parent doesn't complete until all children
// complete; cancelling a parent cancels all children; a failed child cancels the parent
// (and siblings) by default.

data class FullProfile(val user: String, val posts: List<String>)

suspend fun fetchUser(): String {
    delay(100)
    return "Ömer"
}

suspend fun fetchPosts(): List<String> {
    delay(150)
    return listOf("p1", "p2")
}

suspend fun loadProfile(): FullProfile = coroutineScope {   // parallel decomposition
    val user = async { fetchUser() }    // child 1
    val posts = async { fetchPosts() }  // child 2
    FullProfile(user.await(), posts.await())
    // if fetchUser throws → posts is auto-cancelled → coroutineScope rethrows
}

fun main() = runBlocking {
    println(loadProfile())
}

// - CoroutineScope = a CoroutineContext holder; its Job is the hierarchy root.
// - GlobalScope = no parent, no lifecycle → leaks; almost always wrong.
// - coroutineScope { }: creates a child scope, waits for all children, rethrows failures.
