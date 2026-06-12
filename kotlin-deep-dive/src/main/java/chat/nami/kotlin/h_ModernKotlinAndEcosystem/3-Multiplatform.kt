package chat.nami.kotlin.h_ModernKotlinAndEcosystem

// Multiplatform (awareness level) — needs a KMP module; does not compile in a single-target one.
//
// commonMain:
// expect fun platformName(): String
//
// androidMain:
// actual fun platformName() = "Android ${Build.VERSION.SDK_INT}"
//
// iosMain:
// actual fun platformName() = UIDevice.currentDevice.systemName
//
// - expect/actual: compile-time contract; every target must provide actual. Modern guidance:
//   prefer INTERFACES + DI for large abstractions; expect/actual for small leaf functions.
// - Kotlin/Native memory model: the old strict model (frozen objects,
//   InvalidMutabilityException) is GONE since 1.7.20 — new MM is a tracing GC;
//   shared mutable state allowed (same discipline as JVM).
// - Kotlin/JS compiles via IR; Kotlin/Wasm is the newer frontier.
