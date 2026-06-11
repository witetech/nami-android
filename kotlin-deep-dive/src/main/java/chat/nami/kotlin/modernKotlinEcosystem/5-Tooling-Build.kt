package chat.nami.kotlin.modernKotlinEcosystem

// Tooling & build — build-level topic, no runnable demo.
//
// build.gradle.kts
// kotlin {
//     explicitApi()                       // library mode: explicit visibility + return types
//     compilerOptions {
//         freeCompilerArgs.addAll("-Xjvm-default=all", "-progressive")
//         allWarningsAsErrors.set(true)
//     }
// }
//
// gradle/libs.versions.toml — version catalog (this repo uses one!):
// [versions]
// kotlin = "2.3.21"
// coroutines = "1.10.2"
// [libraries]
// kotlinx-coroutines = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
//
// detekt custom rule (needs detekt-api on the classpath of a detekt-rules module):
//
// class NoRunCatchingRule(config: Config) : Rule(config) {
//     override val issue = Issue(
//         "NoRunCatching", Severity.Defect,
//         "Use runCatchingCancellable — runCatching swallows CancellationException",
//         Debt.FIVE_MINS
//     )
//     override fun visitCallExpression(expression: KtCallExpression) {
//         super.visitCallExpression(expression)
//         if (expression.calleeExpression?.text == "runCatching")
//             report(CodeSmell(issue, Entity.from(expression), issue.description))
//     }
// }
//
// Closes the loop with errorModeling/1-RunCatching-Trap.kt — encode the lessons of this
// deck as automated rules.
