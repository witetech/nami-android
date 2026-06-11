package chat.nami.kotlin.compilerInteropMetaprogramming

import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

fun main() {
    val kClass = Vehicle::class
    println(kClass.simpleName)                                  // works WITHOUT kotlin-reflect
    kClass.memberProperties.forEach { println(it.name) }        // needs kotlin-reflect
    val instance = kClass.primaryConstructor!!.call("34 ABC 123", 77.4)
    println(instance)
}

// - kotlin-reflect is a ~3MB artifact; full reflection parses @Metadata at runtime —
//   slow first access, cached after.
// - WITHOUT it: ::class, simple callable references, ::prop.isInitialized still work
//   ("lite" reflection). memberProperties, primaryConstructor throw.
// - R8/ProGuard strips @Metadata and renames symbols → reflection breaks silently in
//   release builds; you need keep rules. This is precisely why kotlinx.serialization
//   (compile-time codegen) beats Gson/reflective Moshi on Android: no keep-rule
//   whack-a-mole, no reflective cost, full R8 shrinking.
