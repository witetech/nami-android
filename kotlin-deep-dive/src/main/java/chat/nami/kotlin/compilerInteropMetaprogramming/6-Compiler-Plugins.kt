package chat.nami.kotlin.compilerInteropMetaprogramming

// Compiler plugins: how Compose & Serialization work — no runnable demo without the plugins.
//
// Both are IR TRANSFORMATION plugins — they rewrite the compiler's intermediate
// representation after type-checking:
//
// Compose: rewrites every @Composable function — injects a Composer parameter + a
// changed: Int bitmask, wraps bodies in startRestartGroup/endRestartGroup, generates
// skip logic ("all params unchanged → skip"). This is why @Composable functions can't
// be called from normal functions: their real signature is different.
//
// What you write:           @Composable fun Greeting(name: String) { Text("Hi $name") }
// What the plugin compiles: fun Greeting(name: String, $composer: Composer, $changed: Int) { ... }
//
// kotlinx.serialization: for each @Serializable class, generates a nested $serializer
// object with a SerialDescriptor + serialize/deserialize — pure generated code, zero
// runtime reflection. (Needs the plugin + runtime to compile:)
//
// plugins { kotlin("plugin.serialization") version "2.1.20" }
// import kotlinx.serialization.*
// import kotlinx.serialization.json.Json
//
// @Serializable
// data class Station(val id: String, val powerKw: Int)
//
// fun main() {
//     val json = Json.encodeToString(Station("zes-01", 180))
//     println(json)                                          // {"id":"zes-01","powerKw":180}
//     println(Json.decodeFromString<Station>(json))
// }
//
// Same mechanism family: all-open, no-arg, Parcelize, Power-assert. Most plugins still
// use internal compiler APIs — which is why Kotlin version bumps break them.
