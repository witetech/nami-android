package chat.nami.kotlin.b_FunctionsAndFunctionalStyle

class Dialog {
    var title: String = ""
    var cancelable: Boolean = true

    override fun toString() = "Dialog(title=$title, cancelable=$cancelable)"
}

fun main() {
    // configure, return receiver
    val dialog = Dialog().apply {
        title = "Hi"
        cancelable = false
    }.also { println("created: $it") } // side effect, return receiver

    // null-safe transform
    val titleLength: Int? = readLine()?.let {
        it.trim().length
    }

    println(titleLength)

    // group calls, return result
    val summary = with(dialog) {
        "$title (cancelable=$cancelable)"
    }
    println(summary)
}
