package chat.nami.kotlin.compilerInteropMetaprogramming

// KAPT vs KSP — no runnable demo; build-level topic.
//
// | | KAPT | KSP |
// |---|---|---|
// | Mechanism | generates Java STUBS, runs javac annotation processing | direct Kotlin compiler API, Kotlin symbol model |
// | Speed | slow (stub generation ≈ an extra compile) | ~2x faster typical |
// | Kotlin awareness | "Java view" (no nullability, no suspend semantics) | real Kotlin types |
// | Status | maintenance mode | the path forward (Room, Hilt, Moshi, Glide support it) |
//
// build.gradle.kts — migration:
// plugins { id("com.google.devtools.ksp") version "2.1.20-2.0.1" }
// dependencies {
//     // kapt("androidx.room:room-compiler:2.7.0")   // before
//     ksp("androidx.room:room-compiler:2.7.0")        // after
// }
//
// KSP2 (with K2) runs as a standalone tool, no longer embedded in the compiler.
