package chat.nami.kotlin.c_ClassesInitializationAndLifecycle

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object FakePrefs {
    private val store = mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> read(key: String, default: T): T = store.getOrDefault(key, default) as T

    fun <T> write(key: String, value: T) {
        store[key] = value
    }
}

class Preference<T>(private val key: String, private val default: T) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = FakePrefs.read(key, default)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        FakePrefs.write(key, value)
}

class Settings {
    val config: String by lazy {                          // default mode: SYNCHRONIZED
        println("loading config once...")
        "loaded"
    }

    val cheap: String by lazy(LazyThreadSafetyMode.NONE) { "single-thread only" }

    var name: String by Delegates.observable("init") { prop, old, new ->
        println("${prop.name}: $old → $new")
    }

    var size: Int by Delegates.vetoable(0) { _, _, new -> new >= 0 }  // reject negatives

    var darkMode: Boolean by Preference("dark_mode", false)
}

fun main() {
    val s = Settings()

    println(s.config); println(s.config)   // "loading..." printed once

    s.name = "Kilowatt"                     // observable fires

    s.size = -5                             // vetoed
    println(s.size)                         // 0

    s.darkMode = true
    println(s.darkMode)                     // true (via FakePrefs)
}
