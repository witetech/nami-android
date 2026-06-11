package chat.nami.kotlin.errorModeling

import java.io.IOException

// Kotlin has no checked exceptions. A Java caller can't even write catch (IOException e)
// around a Kotlin call (javac: "never thrown") unless you declare it:

class Config(val raw: String)

@Throws(IOException::class)
fun readConfig(path: String): Config {
    if (path.isEmpty()) throw IOException("no path")
    return Config("contents of $path")
}

fun main() {
    println(readConfig("/etc/app.conf").raw)
}
