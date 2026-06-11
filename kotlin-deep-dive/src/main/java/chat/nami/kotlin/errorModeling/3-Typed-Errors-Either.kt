package chat.nami.kotlin.errorModeling

// The Arrow pattern, expressed with plain sealed types:

sealed interface LoginError {
    data object InvalidCredentials : LoginError
    data class Network(val code: Int) : LoginError
}

sealed interface Outcome<out E, out T> {
    data class Failure<E>(val error: E) : Outcome<E, Nothing>
    data class Success<T>(val value: T) : Outcome<Nothing, T>
}

data class Session(val token: String)

fun login(user: String, pass: String): Outcome<LoginError, Session> {
    if (user.isBlank() || pass.isBlank()) return Outcome.Failure(LoginError.InvalidCredentials)
    if (user == "down") return Outcome.Failure(LoginError.Network(503))
    return Outcome.Success(Session("token-$user"))
}

fun main() {
    when (val r = login("omer", "secret")) {       // exhaustive over YOUR error type
        is Outcome.Failure -> when (val e = r.error) {
            LoginError.InvalidCredentials -> println("bad credentials")
            is LoginError.Network -> println("network ${e.code}")
        }
        is Outcome.Success -> println("logged in: ${r.value.token}")
    }
}

// With Arrow this becomes Either<LoginError, Session> + the either { } / ensure DSL.
// The point either way: expected failures in the type signature, exceptions only for
// bugs/unrecoverable states.
