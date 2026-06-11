# Kotlin Deep Dive — Study Notes

> Advanced Android Course: From Code to Production
> Companion reference for the Kotlin Deep Dive session(s).
> All snippets are self-contained: copy-paste into a scratch file (`.kts` or a `main`) and they compile. Android-only examples are marked.

---

## 1. Type System & Language Core

### 1.1 Generics: Variance

**Declaration-site variance** — declared on the class:

```kotlin
open class Animal(val name: String)
class Cat(name: String) : Animal(name)

// `out` = covariant: T only appears in output positions (return types)
interface Producer<out T> {
    fun produce(): T
    // fun consume(item: T)  // ❌ compile error: T in `in` position
}

// `in` = contravariant: T only in input positions (parameters)
interface Consumer<in T> {
    fun consume(item: T)
}

class CatProducer : Producer<Cat> {
    override fun produce() = Cat("Tekir")
}

class AnimalConsumer : Consumer<Animal> {
    override fun consume(item: Animal) = println("feeding ${item.name}")
}

fun main() {
    val animalProducer: Producer<Animal> = CatProducer()   // ✅ Producer<Cat> is a Producer<Animal>
    val catConsumer: Consumer<Cat> = AnimalConsumer()      // ✅ Consumer<Animal> is a Consumer<Cat>
    println(animalProducer.produce().name)
    catConsumer.consume(Cat("Pamuk"))
}
```

Mnemonic: **PECS** in Kotlin terms — Producer `out`, Consumer `in`.

**Use-site variance (type projection)** — when the class is invariant but you need variance at the call site:

```kotlin
fun copyInto(from: Array<out Any>, to: Array<Any>) {
    for (i in from.indices) to[i] = from[i]
    // `from` is projected: you can read Any, you cannot write into it
    // from[0] = "x"  // ❌ compile error
}

fun main() {
    val source: Array<String> = arrayOf("a", "b")
    val target: Array<Any> = arrayOf(1, 2)
    copyInto(source, target)
    println(target.toList())   // [a, b]
}
```

**Star projection** — "I don't know the type argument, but I'll work safely":

```kotlin
fun printAll(list: List<*>) {           // == List<out Any?>
    list.forEach { println(it) }        // reads as Any?
}

fun fillFirst(array: Array<*>) {
    // array[0] = "x"                   // ❌ compile error — type unknown
    println("size=${array.size}, first=${array.firstOrNull()}")
}

fun main() {
    printAll(listOf(1, "two", 3.0))
    fillFirst(arrayOf("a", "b"))
}
```

`MutableList<*>` ≠ `MutableList<Any?>`. The first means "a list of *some specific* unknown type"; you can't add anything to it.

### 1.2 Definitely Non-Nullable Types: `T & Any`

Solves the problem of generic functions that must return non-null even when `T` could be nullable:

```kotlin
fun <T> elvisLike(x: T, default: T & Any): T & Any = x ?: default

fun main() {
    val result: String = elvisLike<String?>(null, "fallback") // returns String, not String?
    println(result)   // fallback
}
```

Main use case: overriding Java methods annotated `@NotNull` with generic parameters.

### 1.3 Platform Types & Intersection Types

```java
// Java side — no nullability annotation:
public class JavaUser {
    public String getName() { return null; }   // legal in Java
}
```

```kotlin
// Requires JavaUser.java (above) compiled on the classpath:
fun main() {
    val javaUser = JavaUser()
    val name = javaUser.name      // type is String! (platform type)
    // println(name.length)       // 💥 NPE possible at runtime, compiler stays silent
    val safe: String? = javaUser.name   // ✅ declare explicit type at the boundary
    println(safe?.length)
}
```

- `String!` means "String or String?, compiler doesn't know."
- NPE happens at the *assignment boundary* when assigning to non-null — Kotlin injects `Intrinsics.checkNotNullExpressionValue`.
- Defense: annotate Java code (`@Nullable`/`@NotNull`) or declare explicit nullable types at the boundary.

Intersection types are inferred (you can't write them except `& Any`):

```kotlin
fun <T> firstIfBigger(a: T, b: T): T where T : CharSequence, T : Comparable<T> {
    // inside, T is effectively CharSequence & Comparable<T>
    return if (a > b) a else b
}

fun main() {
    println(firstIfBigger("banana", "apple"))   // banana
}
```

### 1.4 Sealed Classes/Interfaces & Exhaustive `when`

```kotlin
data class Item(val title: String)

sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: List<Item>) : UiState
    data class Error(val cause: Throwable) : UiState
}

fun render(state: UiState): String = when (state) {  // exhaustive — no `else` needed
    UiState.Loading -> "spinner"
    is UiState.Success -> "list of ${state.data.size}"   // smart cast
    is UiState.Error -> "error: ${state.cause.message}"
}

fun main() {
    println(render(UiState.Loading))
    println(render(UiState.Success(listOf(Item("a")))))
    println(render(UiState.Error(IllegalStateException("boom"))))
}
```

Key points:
- Since 1.7, non-exhaustive `when` on a sealed type is an **error** even as a statement.
- `sealed interface` (1.5+) allows multiple sealed hierarchies per class; subclasses can live anywhere in the same module + package.
- Sealed vs enum: enum = fixed set of *instances*; sealed = fixed set of *types* (each can carry different data).

### 1.5 Contracts

Tell the compiler facts it can't infer, enabling smart casts across function boundaries:

```kotlin
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun requireNotBlank(value: String?): String {
    contract { returns() implies (value != null) }
    if (value.isNullOrBlank()) throw IllegalArgumentException("blank")
    return value
}

fun main() {
    val s: String? = "hello"
    requireNotBlank(s)
    println(s.length)   // ✅ smart cast — compiler trusts the contract
}
```

Standard library uses this everywhere: `require`, `check`, `isNullOrEmpty`, and `callsInPlace` (which is why a `val` can be assigned inside `run { }`):

```kotlin
fun main() {
    val x: Int
    run {        // stdlib contract: callsInPlace(block, EXACTLY_ONCE)
        x = 42   // ✅ legal because compiler knows the block runs exactly once
    }
    println(x)
}
```

⚠️ Contracts are **not verified** — a wrong contract is a lie to the compiler and produces unsound code.

### 1.6 Operator Overloading & Convention Methods

Kotlin resolves operators **by convention** (name + `operator` modifier), not by interface:

```kotlin
import java.math.BigDecimal

class Money(val amount: BigDecimal) : Comparable<Money> {
    operator fun plus(other: Money) = Money(amount + other.amount)
    override operator fun compareTo(other: Money) = amount.compareTo(other.amount)
    override fun toString() = "₺$amount"
}

class Router {
    operator fun invoke(path: String) = println("navigating to $path")
}

fun main() {
    val a = Money(BigDecimal("10.50"))
    val b = Money(BigDecimal("4.50"))
    println(a + b)                 // plus convention → ₺15.00
    println(a > b)                 // compareTo convention → true

    val router = Router()
    router("/home")                // invoke convention
}

// Other conventions:
// getValue/setValue → property delegation
// component1..componentN → destructuring
// contains → `in`, get/set → indexing, iterator → for-loops
```

### 1.7 Destructuring Internals

```kotlin
data class User(val id: Int, val name: String)

fun main() {
    val user = User(1, "Ömer")
    val (id, name) = user
    // compiles to:
    // val id = user.component1()
    // val name = user.component2()
    println("$id $name")

    // In lambdas — ONE destructured parameter vs TWO parameters:
    val map = mapOf(1 to "a", 2 to "b")
    map.forEach { (key, value) -> println("$key=$value") }   // one Entry, destructured
    map.mapValues { entry -> entry.value.uppercase() }       // one parameter, no destructuring
}
```

- Data classes generate `componentN` **in declaration order** — reordering properties silently breaks destructuring call sites. Positional, not named!
- K2 2.x roadmap: name-based destructuring to fix the positional fragility.

### 1.8 Infix Functions & DSL Building

```kotlin
@DslMarker
annotation class HtmlDsl

@HtmlDsl
class Div {
    fun text(s: String) = println("  text: $s")
}

@HtmlDsl
class Body {
    fun div(block: Div.() -> Unit) {
        println("<div>")
        Div().block()
        println("</div>")
    }
}

fun body(block: Body.() -> Unit) = Body().block()

infix fun Int.pow(exp: Int): Long {
    var r = 1L; repeat(exp) { r *= this }; return r
}

fun main() {
    println(2 pow 10)   // 1024 — infix

    body {
        div {
            text("ok")
            // div { }   // ❌ compile error thanks to @DslMarker —
            //           // without it, this would silently call body.div
        }
    }
}
```

`@DslMarker` makes nested receivers of the *same marker* shadow outer ones — outer access requires explicit `this@body`. This is how `kotlinx.html` and Gradle Kotlin DSL prevent scoping bugs.

### 1.9 Extension Function Resolution (Static Dispatch!)

```kotlin
open class Base
class Derived : Base()

fun Base.describe() = "base"
fun Derived.describe() = "derived"

fun main() {
    val x: Base = Derived()
    println(x.describe())   // "base" — resolved by STATIC type, at compile time
}
```

- Extensions compile to **static methods**; no virtual dispatch.
- **Member always wins** over an extension with the same signature — adding a member later silently hijacks extension call sites.
- Extension on nullable receiver — `this` can be null inside:

```kotlin
fun String?.orDash(): String = this ?: "—"

fun main() {
    val s: String? = null
    println(s.orDash())   // — (no safe call needed)
}
```

---

## 2. Functions & Functional Style

### 2.1 inline / noinline / crossinline — Bytecode Impact

```kotlin
inline fun measureNanos(block: () -> Unit): Long {
    val start = System.nanoTime()
    block()
    return System.nanoTime() - start
}

fun main() {
    val t = measureNanos { Thread.sleep(10) }
    println("took $t ns")   // no Function0 allocated — body copied into main()
}
```

What `inline` does:
- Copies the function body **and the lambda body** into the call site → zero `Function0` allocation, no virtual `invoke()`.
- Enables **non-local returns** (`return` inside the lambda returns from the enclosing function).
- Enables `reified` type parameters.

```kotlin
val pending = mutableListOf<() -> Unit>()

fun saveForLater(action: () -> Unit) { pending += action }

inline fun process(
    crossinline onEach: (Int) -> Unit,  // inlined, but non-local return FORBIDDEN
    noinline onDone: () -> Unit          // NOT inlined — real object, can escape
) {
    val runnable = Runnable { onEach(1) }  // crossinline: usable in another execution context
    runnable.run()
    saveForLater(onDone)                    // noinline: can be stored/passed
}

fun main() {
    process(onEach = { println("item $it") }, onDone = { println("done") })
    pending.forEach { it() }
}
```

- `noinline`: needed when the lambda must be stored, passed to a non-inline function, or used as a value.
- `crossinline`: lambda is still inlined but called from a nested context → non-local return would be unsound, so it's banned.

When NOT to inline: large bodies (code bloat at every call site), no lambda parameters (compiler warns: negligible benefit).

### 2.2 Reified Generics & Type Erasure

JVM erases generics: `List<String>` and `List<Int>` are the same class at runtime. `reified` + `inline` defeats this — the body is copied to the call site, so the compiler substitutes the *actual* type:

```kotlin
inline fun <reified T> List<Any>.filterType(): List<T> =
    filterIsInstance<T>()                       // stdlib version of the same trick

inline fun <reified T> typeName(): String = T::class.simpleName ?: "?"

fun main() {
    val mixed: List<Any> = listOf(1, "two", 3, "four")
    println(mixed.filterType<String>())   // [two, four]
    println(typeName<Int>())              // Int — survives erasure
}
```

Android version of the same pattern (does not compile in a scratch file — needs the SDK):

```kotlin
// Android-only:
// inline fun <reified T : Activity> Context.start() =
//     startActivity(Intent(this, T::class.java))
// context.start<DetailActivity>()
```

Limits: only in inline functions; can't call `T()` directly; reified type is still erased inside *non-inline* code you call.

### 2.3 Lambda Allocation Cost & Function References

```kotlin
fun isPositive(n: Int) = n > 0

// Our OWN non-inline higher-order function (stdlib filter IS inline → free):
fun applyTwice(n: Int, f: (Int) -> Int): Int = f(f(n))

fun main() {
    val nums = listOf(-1, 2, -3, 4)
    println(nums.filter { it > 0 })       // inline → no allocation
    println(nums.filter(::isPositive))    // unbound ref → singleton

    var offset = 10
    println(applyTwice(1) { it + offset }) // capturing lambda → NEW object per call site execution
    println(applyTwice(1) { it + 1 })      // non-capturing → singleton, allocated once
}
```

- **Non-capturing lambdas** → compiled to a singleton, reused.
- **Capturing lambdas** → new object allocation; in hot paths (`onDraw`, scroll listeners) this churns the GC.
- Captured `var` is boxed into a `Ref.IntRef`/`ObjectRef` wrapper — extra allocation + indirection.
- Bound references (`obj::foo`) capture the receiver; unbound (`::foo`) are singletons.
- Stdlib collection operators are `inline` → cost appears with **your own non-inline** higher-order functions and stored lambdas.

### 2.4 Scope Functions — When Each Actually Fits

| Function | Receiver | Returns | Idiomatic use |
|---|---|---|---|
| `let` | `it` | lambda result | null-safe chains: `x?.let { }`; narrowing scope |
| `run` | `this` | lambda result | configure + compute a result |
| `with` | `this` | lambda result | grouping calls on an existing object (not null-safe) |
| `apply` | `this` | **receiver** | object configuration / builder style |
| `also` | `it` | **receiver** | side effects in a chain (logging, validation) |

```kotlin
class Dialog {
    var title: String = ""
    var cancelable: Boolean = true
    override fun toString() = "Dialog(title=$title, cancelable=$cancelable)"
}

fun main() {
    val dialog = Dialog().apply {            // configure, return receiver
        title = "Hi"
        cancelable = false
    }.also { println("created: $it") }        // side effect, return receiver

    val titleLength: Int? = readLine()?.let { // null-safe transform, return result
        it.trim().length
    }
    println(titleLength)

    val summary = with(dialog) {              // group calls, return result
        "$title (cancelable=$cancelable)"
    }
    println(summary)
}
```

Anti-patterns: nesting 3+ scope functions (`it`/`this` ambiguity), `let` on non-null values just to chain, using `apply` when you actually needed the lambda result.

### 2.5 Local Functions, Closures, tailrec

```kotlin
data class Account(val id: Int, val name: String, val email: String)

fun validate(account: Account) {
    fun requireField(value: String, field: String) {     // closure: captures `account`
        require(value.isNotEmpty()) { "$field empty for account ${account.id}" }
    }
    requireField(account.name, "name")
    requireField(account.email, "email")
}

tailrec fun gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)   // compiled to a loop — no stack frames

fun main() {
    validate(Account(1, "Ömer", "omer@wite.com.tr"))
    println(gcd(48, 18))   // 6
}
```

`tailrec` requires the recursive call to be the **last operation**. `n * factorial(n - 1)` is NOT tail-recursive (multiplication happens after the call).

---

## 3. Classes, Initialization & Lifecycle

### 3.1 Initialization Order & Leaking `this`

Execution order: **superclass constructor → property initializers + `init` blocks in source order → constructor body**.

```kotlin
open class BaseScreen {
    open val size: Int = 10
    init { println("BaseScreen init, size = $size") }   // calls the OVERRIDDEN getter!
}

class DerivedScreen : BaseScreen() {
    override val size: Int = 20
}

fun main() {
    DerivedScreen()  // prints "BaseScreen init, size = 0"
                     // ← DerivedScreen.size backing field not yet initialized!
}
```

**Rule: never call open members or read open properties in constructors/init blocks.** Same bug family: passing `this` out of a constructor (registering listeners, starting threads) — the object isn't fully constructed yet ("leaking this").

### 3.2 lateinit Internals

```kotlin
class ListAdapter(val label: String)

class Screen {
    lateinit var adapter: ListAdapter

    fun bindIfReady() {
        if (::adapter.isInitialized) {        // reflection-free check via property reference
            println("bound: ${adapter.label}")
        } else {
            println("not initialized yet")
        }
    }
}

fun main() {
    val screen = Screen()
    screen.bindIfReady()                       // not initialized yet
    screen.adapter = ListAdapter("items")
    screen.bindIfReady()                       // bound: items
}
```

- Bytecode: a plain non-null field, **no flag** — the "initialized" check is just `field != null`. That's why `lateinit` works only for non-null reference types, not primitives (no null sentinel for `Int`).
- Each read injects a null check throwing `UninitializedPropertyAccessException` with the property name.

| Choice | When |
|---|---|
| `lateinit var` | DI / lifecycle injection; you guarantee init-before-use; vars only |
| Nullable `T?` | "absent" is a legitimate state |
| `Delegates.notNull()` | primitives needing lateinit semantics (boxes — minor cost) |
| `by lazy` | `val`, computed on first access |

### 3.3 Delegated Properties

```kotlin
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// Stand-in for SharedPreferences:
object FakePrefs {
    private val store = mutableMapOf<String, Any?>()
    @Suppress("UNCHECKED_CAST")
    fun <T> read(key: String, default: T): T = store.getOrDefault(key, default) as T
    fun <T> write(key: String, value: T) { store[key] = value }
}

class Preference<T>(private val key: String, private val default: T) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = FakePrefs.read(key, default)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = FakePrefs.write(key, value)
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
```

- `lazy` modes: `SYNCHRONIZED` (default), `PUBLICATION` (multiple threads may compute, first wins), `NONE` (no sync, fastest).
- Since 1.4: `provideDelegate` operator lets a delegate intercept its own creation (used by Gradle DSL).
- Bytecode: a hidden `$delegate` field per property + a `KProperty` metadata array.

### 3.4 companion object vs Top-Level, const

```kotlin
const val APP_NAME = "Kilowatt"     // top-level const — inlined into consumers at compile time

class Repo private constructor() {
    companion object {
        const val TIMEOUT_SECONDS = 30        // inlined at call sites
        val createdAt = System.currentTimeMillis()  // accessed via Companion instance
        @JvmStatic fun create() = Repo()      // real static method for Java callers
    }
}

fun main() {
    println(APP_NAME)
    println(Repo.TIMEOUT_SECONDS)
    println(Repo.createdAt)
    val repo = Repo.create()                  // Java without @JvmStatic: Repo.Companion.create()
    println(repo)
}
```

- `companion object` = a real singleton object holding the members.
- `const val`: compile-time constant (primitives + String, top-level or inside object). Values are **inlined into consumers** — changing a `const` in a library requires consumers to recompile.
- Top-level declarations compile to a `FileNameKt` class with static members — cheaper than a companion, preferred for pure functions/constants.

### 3.5 Singleton Thread-Safety

```kotlin
object AppConfig {
    val map: Map<String, String> = run {
        println("initializing AppConfig...")     // runs once, on first access
        mapOf("env" to "prod")
    }
}

enum class LegacySingleton { INSTANCE;
    fun doWork() = println("enum singleton working")
}

fun main() {
    println("before first access")
    println(AppConfig.map["env"])    // init happens HERE, thread-safe
    LegacySingleton.INSTANCE.doWork()
}
```

Both rely on **JVM class-initialization guarantees** (class init is locked by the JVM) → safe without explicit synchronization. `object` initialization runs on first access, like a `lazy` with class-loading semantics.

### 3.6 value class vs data class — Boxing Behavior

```kotlin
@JvmInline
value class UserId(val raw: Long) {
    init { require(raw > 0) { "invalid id" } }
}

fun load(id: UserId) = println("loading ${id.raw}")
// compiles to load-<hash>(J) — a raw long parameter, ZERO allocation

fun main() {
    load(UserId(42))                        // unboxed: passed as long

    val nullable: UserId? = UserId(1)       // boxed (nullable over a primitive)
    val inList: List<UserId> = listOf(UserId(2))  // boxed (generic)
    println("$nullable, $inList")
}
```

Boxing happens when the value class is used **as a reference type**:

| Context | Boxed? |
|---|---|
| Function param / return / local | ❌ no (underlying type) |
| Nullable `UserId?` (over a primitive) | ✅ yes |
| Generic: `List<UserId>`, `T` | ✅ yes |
| Used as an interface type it implements | ✅ yes |
| `===` comparison | ✅ (and meaningless) |

- Mangled names (`load-impl`): prevent JVM signature clashes between `f(UserId)` and `f(Long)`. Java interop pain point — fix with `@JvmName`.
- vs data class: value class = 1 property, no identity, no `copy()`, goal = type safety at zero cost. Data class = N properties, identity, heap object.

---

## 4. Equality, Immutability & State

### 4.1 == vs ===, equals/hashCode Contracts

```kotlin
data class Point(val x: Int)   // equals/hashCode generated from primary ctor properties

data class Profile(val id: Int) {
    var nickname: String = ""  // body property — IGNORED by equals/hashCode!
}

fun main() {
    val a = Point(1); val b = Point(1)
    println(a == b)    // true  — structural: a?.equals(b) ?: (b === null)
    println(a === b)   // false — referential: different objects

    // Boxed-int cache trap (-128..127):
    val x: Int? = 100; val y: Int? = 100
    val p: Int? = 1000; val q: Int? = 1000
    println(x === y)   // true  — cached Integer instances! never rely on this
    println(p === q)   // false

    // Body properties ignored:
    val u1 = Profile(1).apply { nickname = "A" }
    val u2 = Profile(1).apply { nickname = "B" }
    println(u1 == u2)  // true!

    // Arrays don't override equals:
    println(intArrayOf(1, 2) == intArrayOf(1, 2))             // false
    println(intArrayOf(1, 2).contentEquals(intArrayOf(1, 2))) // true
}
```

Contracts: equal objects MUST have equal hashCodes; override both or neither. Data classes holding arrays need manual `equals`/`hashCode` (`contentDeepEquals`).

### 4.2 Data Class copy Pitfalls

```kotlin
data class LineItem(val sku: String)
data class Order(val id: Int, val items: MutableList<LineItem>)

fun main() {
    val a = Order(1, mutableListOf(LineItem("EV-CABLE")))
    val b = a.copy(id = 2)
    b.items.add(LineItem("ADAPTER"))
    println(a.items)   // [EV-CABLE, ADAPTER] ⚠️ a mutated too — SHALLOW copy
}
```

**Encapsulation leak** — `copy()` stays public even with a private constructor:

```kotlin
data class Email private constructor(val value: String) {
    companion object {
        fun of(s: String): Email? = if ("@" in s) Email(s) else null
    }
}

fun main() {
    val valid = Email.of("a@b.c")!!
    val invalid = valid.copy(value = "not-an-email")   // ⚠️ bypassed factory validation
    println(invalid)                                   // (2.0.20+ warns; @ConsistentCopyVisibility fixes)
}
```

Mitigations: keep only immutable types inside data classes (`List`, not `MutableList`); `@ConsistentCopyVisibility` (2.0.20+) makes `copy` match constructor visibility; or use a normal class.

### 4.3 val ≠ Immutable

```kotlin
fun main() {
    val list = mutableListOf(1, 2)   // reference is fixed; contents are not
    list.add(3)
    println(list)                    // [1, 2, 3]

    val holder = object {
        val time: Long get() = System.nanoTime()   // a val that changes per read!
    }
    println(holder.time == holder.time)            // false
}
```

Deep-immutability strategies:
- Immutable data all the way down: `data class` + `val` + read-only `List`/`Map` + immutable element types.
- `kotlinx.collections.immutable` (`PersistentList`) for *guaranteed* immutability.
- Defensive copies at boundaries:

```kotlin
class Cart(items: List<String>) {
    private val items: List<String> = items.toList()   // defensive copy
    fun items(): List<String> = items
}

fun main() {
    val source = mutableListOf("a")
    val cart = Cart(source)
    source.add("b")               // caller mutates their list...
    println(cart.items())         // [a] — cart unaffected
}
```

### 4.4 The Cast Attack: List → MutableList

```kotlin
class Repo {
    private val _cache = mutableListOf("legit")
    val cache: List<String> get() = _cache        // read-only INTERFACE...
}

fun main() {
    val repo = Repo()
    @Suppress("UNCHECKED_CAST")
    (repo.cache as MutableList<String>).add("evil")   // ...same object — cast succeeds!
    println(repo.cache)   // [legit, evil] 😱
}
```

Kotlin's `List` is a read-only **view**, not an immutability guarantee. Defenses:
- Return a copy: `get() = _cache.toList()`
- `java.util.Collections.unmodifiableList(_cache)` → cast succeeds but mutation throws
- `kotlinx.collections.immutable` → genuinely immutable type

**Backing property pattern** (`_state`/`state`) — the standard ViewModel idiom (needs `kotlinx-coroutines-core`):

```kotlin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface ScreenState {
    data object Loading : ScreenState
    data class Ready(val items: List<String>) : ScreenState
}

class ListViewModel {
    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state.asStateFlow()  // wrapper — cast attack FAILS

    fun load() { _state.value = ScreenState.Ready(listOf("a", "b")) }
}

fun main() {
    val vm = ListViewModel()
    vm.load()
    println(vm.state.value)
    println(vm.state is MutableStateFlow<*>)   // false — asStateFlow() returns a wrapper
}
```

### 4.5 Backing Properties Beyond Flow

```kotlin
class TagStore {
    private val _tags = mutableSetOf<String>()
    val tags: Set<String> get() = _tags.toSet()   // copy on read — fully safe, costs allocation

    fun add(tag: String) { _tags += tag }
}

fun main() {
    val store = TagStore()
    store.add("ev"); store.add("charging")
    println(store.tags)
}
```

---

## 5. Collections & Performance

### 5.1 Sequences vs Collections

```kotlin
fun main() {
    val list = (1..1_000_000).toList()

    // EAGER: 2 intermediate million-element lists, full passes
    val eager = list.filter { it % 2 == 0 }.map { it * 2 }.take(3)

    // LAZY: element-by-element pipeline, stops after 3 results, ZERO intermediate lists
    val lazy = list.asSequence()
        .filter { it % 2 == 0 }
        .map { it * 2 }
        .take(3)
        .toList()

    println(eager)   // [4, 8, 12]
    println(lazy)    // [4, 8, 12]
}
```

Sequences win: large collections + multiple ops, early termination (`take`, `first`, `any`), infinite sources (`generateSequence`).
Collections win: small lists (sequence overhead > savings), needing `size`/index/multiple passes, ops that must materialize anyway (`sorted`).

⚠️ Sequence lambdas are NOT inlined (collection operators are) — for a 5-element list, eager is faster.

### 5.2 Primitive Arrays

```kotlin
fun main() {
    val a: IntArray = intArrayOf(1, 2, 3)       // int[]     — contiguous primitives
    val b: Array<Int> = arrayOf(1, 2, 3)        // Integer[] — every element boxed
    val c: List<Int> = listOf(1, 2, 3)          // ArrayList<Integer> — boxed

    println(a.sum() + b.sum() + c.sum())
}
```

For hot numeric loops (audio, image, sensor data): `IntArray`/`FloatArray` = no boxing, cache-friendly.

### 5.3 ArrayDeque & Persistent Collections

```kotlin
fun main() {
    val undoStack = ArrayDeque<String>()        // kotlin.collections.ArrayDeque — ring buffer
    undoStack.addLast("typed a")
    undoStack.addLast("typed b")
    println(undoStack.removeLast())             // typed b — stack
    undoStack.addFirst("urgent")
    println(undoStack.removeFirst())            // urgent — queue
}
```

Persistent collections (needs `kotlinx-collections-immutable`):

```kotlin
import kotlinx.collections.immutable.persistentListOf

fun main() {
    val v1 = persistentListOf(1, 2)
    val v2 = v1.add(3)        // v1 unchanged; v2 shares structure with v1
    println(v1)               // [1, 2]
    println(v2)               // [1, 2, 3]
}
```

Compose angle: a `List` parameter is *unstable*; `ImmutableList` is stable → fewer recompositions.

### 5.4 Lesser-Known Operators

```kotlin
data class Member(val id: Int, val city: String, val score: Int, val valid: Boolean)

fun main() {
    val users = listOf(
        Member(1, "İstanbul", 90, true),
        Member(2, "Ankara", 70, false),
        Member(3, "İstanbul", 80, true),
    )

    println(users.groupBy { it.city })                 // Map<City, List<Member>>
    println(users.associateBy { it.id })               // Map<Id, Member> (last wins on dup)
    println(users.associateWith { it.score })          // Map<Member, Score>

    val nums = listOf(1, 2, 3, 4)
    println(nums.fold(100) { acc, n -> acc + n })      // 110 — reduce with initial value
    println(nums.runningFold(0) { acc, n -> acc + n }) // [0, 1, 3, 6, 10] — all intermediates

    println(nums.chunked(2))                           // [[1, 2], [3, 4]] — pagination batches
    println(nums.windowed(size = 3, step = 1) { it.average() })  // moving average
    println(nums.zipWithNext { a, b -> b - a })        // [1, 1, 1] — deltas

    println(users.groupingBy { it.city }.eachCount())  // {İstanbul=2, Ankara=1}
    val (valid, invalid) = users.partition { it.valid }
    println("${valid.size} valid, ${invalid.size} invalid")
}
```

---

## 6. Coroutines

> All snippets in this section need `kotlinx-coroutines-core`. Each is a complete runnable program.

### 6.1 Structured Concurrency, Scope, Job Hierarchy

Core idea: **every coroutine has a parent; a parent doesn't complete until all children complete; cancelling a parent cancels all children; a failed child cancels the parent (and siblings) by default.**

```kotlin
import kotlinx.coroutines.*

data class FullProfile(val user: String, val posts: List<String>)

suspend fun fetchUser(): String { delay(100); return "Ömer" }
suspend fun fetchPosts(): List<String> { delay(150); return listOf("p1", "p2") }

suspend fun loadProfile(): FullProfile = coroutineScope {   // parallel decomposition
    val user = async { fetchUser() }    // child 1
    val posts = async { fetchPosts() }  // child 2
    FullProfile(user.await(), posts.await())
    // if fetchUser throws → posts is auto-cancelled → coroutineScope rethrows
}

fun main() = runBlocking {
    println(loadProfile())
}
```

- `CoroutineScope` = a `CoroutineContext` holder; its `Job` is the hierarchy root.
- `GlobalScope` = no parent, no lifecycle → leaks; almost always wrong.
- `coroutineScope { }`: creates a child scope, waits for all children, rethrows failures.

### 6.2 Dispatchers & Context Switching

| Dispatcher | Backing | Use |
|---|---|---|
| `Main` / `Main.immediate` | UI thread | UI; `.immediate` skips re-dispatch if already on Main |
| `Default` | shared pool ≈ CPU cores | CPU work: parsing, diffing, sorting |
| `IO` | elastic pool (default cap 64) | blocking I/O: Room, files, OkHttp sync |
| `Unconfined` | none | tests/edge cases |

```kotlin
import kotlinx.coroutines.*

suspend fun parseLargeJson(): Int = withContext(Dispatchers.Default) {
    println("parsing on ${Thread.currentThread().name}")
    (1..1_000_000).sum().toInt()
}

@OptIn(ExperimentalCoroutinesApi::class)
val dbWriter = Dispatchers.IO.limitedParallelism(1)   // serial executor view of IO

suspend fun writeRow(n: Int) = withContext(dbWriter) {
    println("writing $n on ${Thread.currentThread().name}")
}

fun main() = runBlocking {
    println("sum = ${parseLargeJson()}")
    (1..3).map { launch { writeRow(it) } }.joinAll()   // serialized, never concurrent
}
```

- `Default` and `IO` **share threads** — `withContext(Dispatchers.IO)` from Default often doesn't switch threads, just the bookkeeping (cheap).
- `withContext` also acts as a `coroutineScope` (waits for children).
- Best practice: make suspend functions **main-safe** (they hop internally); callers shouldn't need `withContext`.

### 6.3 Cancellation Is Cooperative

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 0
        while (isActive) {           // ✅ cooperative check — without it the loop never stops
            i++
        }
        println("stopped at $i")
    }
    delay(100)
    job.cancelAndJoin()
}
```

`CancellationException` is **normal control flow** — never swallow it; cleanup needs `NonCancellable`:

```kotlin
import kotlinx.coroutines.*

suspend fun releaseRemoteLock() { delay(50); println("lock released") }

fun main() = runBlocking {
    val job = launch {
        try {
            repeat(100) { delay(100); println("working $it") }
        } catch (e: CancellationException) {
            println("cancelled — rethrowing"); throw e          // ALWAYS rethrow
        } finally {
            withContext(NonCancellable) { releaseRemoteLock() }  // suspending cleanup while cancelled
        }
    }
    delay(250)
    job.cancelAndJoin()
}
```

### 6.4 Exception Handling

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // supervisorScope: children fail independently (default Job: one failure kills siblings)
    supervisorScope {
        val handler = CoroutineExceptionHandler { _, e -> println("caught: ${e.message}") }
        launch(handler) { delay(50); error("task A failed") }   // handler works: supervisor child
        launch { delay(100); println("task B survived") }
    }

    // async holds its exception until await():
    supervisorScope {
        val deferred = async { error("async boom") }
        try { deferred.await() } catch (e: IllegalStateException) { println("awaited: ${e.message}") }
    }
}
```

Rules that trip everyone up:
- `launch` propagates exceptions **up the Job tree**; `async` holds until `await()` — but still cancels the parent immediately unless under a SupervisorJob.
- `CoroutineExceptionHandler` on a non-root child is ignored.
- `try/catch` around `launch { }` catches nothing — catch *inside* the coroutine or around `await()`.
- Android: `viewModelScope` = `SupervisorJob() + Dispatchers.Main.immediate`.

### 6.5 Flow

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // COLD: code runs per collector
    val cold: Flow<Int> = flow {
        println("flow body running")
        emit(1); emit(2)
    }
    cold.collect { println("A got $it") }
    cold.collect { println("B got $it") }   // body runs AGAIN

    // HOT:
    val state = MutableStateFlow(0)           // always has a value, conflated, replay=1
    val events = MutableSharedFlow<String>()  // no initial value, configurable replay
    state.value = 5
    println("state now: ${state.value}")
    val sub = launch { events.collect { println("event: $it") } }
    delay(10); events.emit("clicked"); delay(10)
    sub.cancel()
}
```

| | StateFlow | SharedFlow |
|---|---|---|
| Initial value | required | none |
| Replay | exactly 1 (latest) | configurable |
| Conflation | yes — equal values skipped | no |
| Use | UI state | one-shot events* |

\* SharedFlow events are dropped when no collector is active — for critical events model them as state, or use `Channel(...).receiveAsFlow()`.

Operator pipeline (typeahead-search shape):

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun searchApi(q: String): Flow<String> = flow {
    delay(100)                       // simulated network
    emit("results for '$q'")
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val queries = flow {
        emit("k"); delay(50)
        emit("ki"); delay(50)
        emit("kilowatt"); delay(500)     // only this one survives debounce
    }

    queries
        .debounce(200)
        .distinctUntilChanged()
        .flatMapLatest { q -> searchApi(q) }   // cancels previous in-flight search
        .catch { e -> emit("fallback: ${e.message}") }  // catches UPSTREAM only
        .flowOn(Dispatchers.IO)                          // changes UPSTREAM context only
        .collect { println(it) }                          // results for 'kilowatt'
}
```

Backpressure: `flow` suspends the emitter when the collector is slow (natural backpressure). Tuning: `buffer(n)`, `conflate()` (keep latest), `collectLatest { }` (cancel in-progress handling).

Hot sharing: `shareIn(scope, SharingStarted.WhileSubscribed(5000), replay = 1)` / `stateIn(...)` — `5000` ms keeps upstream alive across configuration changes.

### 6.6 Channels vs Flow, select

`Channel` = hot, **one value → one receiver** (fan-out distributes among consumers). Flow → each collector gets everything.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select

fun main() = runBlocking {
    // Fan-out work queue:
    val jobs = Channel<Int>()
    repeat(2) { worker ->
        launch { for (job in jobs) println("worker $worker took job $job") }
    }
    (1..4).forEach { jobs.send(it) }
    jobs.close()
    delay(100)

    // select: race two sources, first wins
    val fast = Channel<String>(); val slow = Channel<String>()
    launch { delay(50); fast.send("fast result") }
    launch { delay(500); slow.send("slow result") }
    val winner = select<String> {
        fast.onReceive { it }
        slow.onReceive { it }
    }
    println(winner)   // fast result
    coroutineContext.cancelChildren()
}
```

### 6.7 Mutex, Semaphore vs synchronized

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

var counter = 0
val mutex = Mutex()
val limiter = Semaphore(permits = 2)

suspend fun fetch(id: Int) = limiter.withPermit {   // max 2 concurrent "requests"
    println("fetching $id"); delay(100)
}

fun main() = runBlocking {
    // Mutex: suspends instead of blocking the thread
    val incrementJobs = List(100) {
        launch(Dispatchers.Default) {
            repeat(100) { mutex.withLock { counter++ } }
        }
    }
    incrementJobs.joinAll()
    println("counter = $counter")   // always 10000

    (1..5).map { launch { fetch(it) } }.joinAll()   // observe batches of 2
}
```

- `synchronized`/`ReentrantLock` **block the thread**; never suspend inside `synchronized` (the monitor is thread-owned; you may resume on a different thread).
- `Mutex` is **not reentrant** — locking twice from the same coroutine deadlocks.
- `synchronized` is fine for short, non-suspending critical sections.

### 6.8 Coroutine Internals: CPS & State Machines

The compiler rewrites every `suspend fun` via **Continuation-Passing Style**:

```kotlin
import kotlinx.coroutines.*

suspend fun stepOne(): String { delay(10); return "A" }
suspend fun stepTwo(prev: String): String { delay(10); return prev + "B" }

suspend fun demo(): String {
    val a = stepOne()      // suspension point → label 0 → 1
    val b = stepTwo(a)     // suspension point → label 1 → 2
    return a + b
}

fun main() = runBlocking { println(demo()) }   // AAB
```

What the compiler actually generates for `demo` (conceptually):

```text
fun demo(completion: Continuation<String>): Any?   // returns String or COROUTINE_SUSPENDED
class DemoContinuation : ContinuationImpl { var label = 0; var a: String? ... }
invokeSuspend() = when (label) { 0 -> ...; 1 -> ...; 2 -> ... }   // locals spilled into fields
```

**Live demo:** Tools → Kotlin → Show Kotlin Bytecode → Decompile on `demo()`. Show the `label` switch and the spilled locals. Explains: why suspend functions need a suspend context (the hidden continuation parameter), and why coroutines are cheaper than threads (a small continuation object vs a ~1MB stack).

---

## 7. Concurrency Beyond Coroutines

### 7.1 JMM in Kotlin Terms, @Volatile, Atomics

```kotlin
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class Worker {
    @Volatile var running = true        // visibility: writes seen by all threads
    val processed = AtomicInteger(0)    // atomicity: increment is CAS, race-free

    fun loop() {
        while (running) { processed.incrementAndGet() }
    }
}

fun main() {
    val w = Worker()
    val t = thread { w.loop() }
    Thread.sleep(50)
    w.running = false       // without @Volatile, t might NEVER see this write
    t.join()
    println("processed ${w.processed.get()}")
}
```

- `@Volatile` ≠ atomic: `running++` would still be a read-modify-write race; that's what atomics are for.
- Multiplatform: `kotlinx-atomicfu` → `val count = atomic(0); count.incrementAndGet()`.
- JMM mental model: without synchronization (`@Volatile`, locks, atomics, or coroutine happens-before edges), one thread's writes may never become visible to another. Coroutine primitives (`launch`, `join`, `Mutex`, channels) establish happens-before — data handed through them is safe.

### 7.2 Thread-Blocking Traps in Coroutines

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // ❌ Blocks a Default pool thread (pool ≈ CPU cores → easy starvation):
    // withContext(Dispatchers.Default) { Thread.sleep(1000) }

    // ❌ runBlocking inside a coroutine on Main → instant deadlock risk
    // ❌ CountDownLatch.await(), future.get(), synchronized + long work

    // ✅ delay suspends; the thread serves other coroutines meanwhile:
    withContext(Dispatchers.Default) { delay(100); println("suspended politely") }

    // ✅ genuinely blocking I/O belongs on IO (the elastic pool exists for exactly this):
    withContext(Dispatchers.IO) {
        @Suppress("BlockingMethodInNonBlockingContext")
        Thread.sleep(100)   // stand-in for a blocking JDBC/file call
        println("blocked on IO pool — acceptable")
    }
}
```

Why `synchronized` doesn't suspend: monitors are **thread-owned**. A coroutine may suspend on thread A and resume on thread B — the monitor would be "held" by A forever. Hence `Mutex` (coroutine-owned, suspension-based) for suspending critical sections.

---

## 8. Error Modeling

### 8.1 Result<T> & the runCatching Trap

```kotlin
import kotlinx.coroutines.*

suspend fun fetchData(): String { delay(1000); return "data" }

fun main() = runBlocking {
    val job = launch {
        // ⚠️ BUG: runCatching catches Throwable — INCLUDING CancellationException.
        // Cancellation is swallowed; the coroutine "completes" with a failure Result.
        val result = runCatching { fetchData() }
        println("swallowed cancellation, got: $result")   // this PRINTS — coroutine didn't cancel properly
    }
    delay(100)
    job.cancelAndJoin()
}
```

Fix — the cancellation-aware variant every codebase ends up writing:

```kotlin
import kotlinx.coroutines.*

inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try { Result.success(block()) }
    catch (e: CancellationException) { throw e }   // let cancellation propagate
    catch (e: Throwable) { Result.failure(e) }

suspend fun fetchData(): String { delay(1000); return "data" }

fun main() = runBlocking {
    val job = launch {
        val result = runCatchingCancellable { fetchData() }
        println("unreachable on cancel: $result")
    }
    delay(100)
    job.cancelAndJoin()
    println("cancelled cleanly")
}
```

Other `Result` notes: it's a value class over `Any?`; failures are untyped (`Throwable`) — you lose the error taxonomy.

### 8.2 Checked Exceptions Interop, @Throws

Kotlin has no checked exceptions. A Java caller can't even write `catch (IOException e)` around a Kotlin call (javac: "never thrown") unless you declare it:

```kotlin
import java.io.IOException

class Config(val raw: String)

@Throws(IOException::class)
fun readConfig(path: String): Config {
    if (path.isEmpty()) throw IOException("no path")
    return Config("contents of $path")
}

fun main() {
    println(readConfig("/etc/app.conf").raw)
}
```

### 8.3 Typed Errors: Either-Style (No Library Needed)

The Arrow pattern, expressed with plain sealed types so the snippet compiles standalone:

```kotlin
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
```

With Arrow this becomes `Either<LoginError, Session>` + the `either { }` / `ensure` DSL. The point either way: **expected failures in the type signature, exceptions only for bugs/unrecoverable states.**

---

## 9. Compiler, Interop & Metaprogramming

### 9.1 KAPT vs KSP

| | KAPT | KSP |
|---|---|---|
| Mechanism | generates Java **stubs**, runs javac annotation processing | direct Kotlin compiler API, Kotlin symbol model |
| Speed | slow (stub generation ≈ an extra compile) | ~2x faster typical |
| Kotlin awareness | "Java view" (no nullability, no suspend semantics) | real Kotlin types |
| Status | maintenance mode | the path forward (Room, Hilt, Moshi, Glide support it) |

```kotlin
// build.gradle.kts — migration:
// plugins { id("com.google.devtools.ksp") version "2.1.20-2.0.1" }
// dependencies {
//     // kapt("androidx.room:room-compiler:2.7.0")   // before
//     ksp("androidx.room:room-compiler:2.7.0")        // after
// }
```

KSP2 (with K2) runs as a standalone tool, no longer embedded in the compiler.

### 9.2 Java Interop Pitfalls

```kotlin
class Utils {
    companion object {
        @JvmStatic fun helper() = println("static for Java")
        // Java without @JvmStatic: Utils.Companion.helper()
    }
}

class Toaster {
    @JvmOverloads
    fun toast(msg: String, duration: Int = 1, gravity: Int = 0) =
        println("toast($msg, $duration, $gravity)")
    // generates 3 overloads for Java; default-value evaluation stays in Kotlin
}

@JvmField val EXPOSED_FIELD = 42       // raw field, no getter

fun main() {
    Utils.helper()
    Toaster().toast("merhaba")
    println(EXPOSED_FIELD)
}
```

- Platform types (§1.3): the #1 interop NPE source. Kotlin adds `checkNotNullParameter` intrinsics on public function params — Java passing null into a non-null Kotlin param fails **fast at the boundary** (good).
- `@JvmName` fixes mangled/clashing names; `@file:JvmName("StringUtils")` renames the `FileKt` facade.
- Kotlin `List` ↔ Java `List`: read-only-ness is NOT enforced for Java callers.

### 9.3 Show Kotlin Bytecode — Recurring Demo Device

Tools → Kotlin → Show Kotlin Bytecode → **Decompile**. Best demos:
1. `inline` vs normal higher-order fn → the lambda class disappears (§2.1)
2. `data class` → generated `equals/hashCode/toString/componentN/copy` (§4)
3. `suspend fun` with 2 suspension points → state machine + `label` (§6.8)
4. `const val` vs `val` in companion → inlining vs `Companion.getX()` (§3.4)
5. `value class` param → mangled method with primitive signature (§3.6)
6. `when` on sealed → `instanceof` chain / tableswitch

### 9.4 kotlin-reflect: Cost & R8

Needs the `kotlin-reflect` artifact on the classpath:

```kotlin
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

data class Vehicle(val plate: String, val kwh: Double)

fun main() {
    val kClass = Vehicle::class
    println(kClass.simpleName)                                  // works WITHOUT kotlin-reflect
    kClass.memberProperties.forEach { println(it.name) }        // needs kotlin-reflect
    val instance = kClass.primaryConstructor!!.call("34 ABC 123", 77.4)
    println(instance)
}
```

- `kotlin-reflect` is a ~3MB artifact; full reflection parses `@Metadata` at runtime — slow first access, cached after.
- **Without** it: `::class`, simple callable references, `::prop.isInitialized` still work ("lite" reflection). `memberProperties`, `primaryConstructor` throw.
- R8/ProGuard strips `@Metadata` and renames symbols → reflection breaks silently in release builds; you need keep rules. This is precisely why **kotlinx.serialization (compile-time codegen) beats Gson/reflective Moshi** on Android: no keep-rule whack-a-mole, no reflective cost, full R8 shrinking.

### 9.5 Annotations: Retention & Targets

```kotlin
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
// SOURCE: visible to KSP/lint only; BINARY: in .class, not reflectable; RUNTIME: reflectable
annotation class Redacted

data class Citizen(@param:Redacted val ssn: String, val name: String) {
    override fun toString() = "Citizen(ssn=███, name=$name)"
}

fun main() {
    println(Citizen("12345678901", "Ömer"))
}
```

Use-site targets matter on constructor properties — one declaration maps to param + field + getter; `@param:`, `@field:`, `@get:` disambiguate (classic: `@field:Inject` for field injection).

### 9.6 Compiler Plugins: How Compose & Serialization Work

Both are **IR transformation** plugins — they rewrite the compiler's intermediate representation after type-checking:

- **Compose**: rewrites every `@Composable` function — injects a `Composer` parameter + a `changed: Int` bitmask, wraps bodies in `startRestartGroup/endRestartGroup`, generates skip logic ("all params unchanged → skip"). This is why `@Composable` functions can't be called from normal functions: their real signature is different.

```kotlin
// What you write:           @Composable fun Greeting(name: String) { Text("Hi $name") }
// What the plugin compiles: fun Greeting(name: String, $composer: Composer, $changed: Int) { ... }
```

- **kotlinx.serialization**: for each `@Serializable` class, generates a nested `$serializer` object with a `SerialDescriptor` + `serialize`/`deserialize` — pure generated code, zero runtime reflection. (Needs the plugin + runtime to compile:)

```kotlin
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
```

- Same mechanism family: `all-open`, `no-arg`, Parcelize, Power-assert. Most plugins still use internal compiler APIs — which is why Kotlin version bumps break them.

---

## 10. Modern Kotlin & Ecosystem

### 10.1 K2 Compiler (Kotlin 2.0+)

- Complete frontend rewrite (FIR): up to ~2x compile speed, one unified resolution structure.
- **Smarter smart casts** — stable across more scopes:

```kotlin
fun describe(x: Any): String {
    val isString = x is String
    return if (isString) "string of length ${x.length}"   // ✅ K2 smart-casts via a boolean variable
    else "not a string"
}

fun main() {
    println(describe("kotlin"))
    println(describe(42))
}
```

### 10.2 New Language Features (2.1+)

Guard conditions in `when` (requires `-Xwhen-guards` compiler flag on 2.1.x; stable in 2.2):

```kotlin
// Compile with: kotlinc -Xwhen-guards
sealed interface LoadState {
    data class Success(val data: List<String>) : LoadState
    data object Error : LoadState
}

fun render(state: LoadState): String = when (state) {
    is LoadState.Success if state.data.isEmpty() -> "empty view"
    is LoadState.Success -> "list of ${state.data.size}"
    LoadState.Error -> "error view"
}

fun main() {
    println(render(LoadState.Success(emptyList())))
    println(render(LoadState.Success(listOf("a"))))
}
```

Non-local `break`/`continue` through inline lambdas (language version 2.2; on 2.1.x compile with `-language-version 2.2`):

```kotlin
// Compile with: kotlinc -language-version 2.2
fun main() {
    outer@ for (batch in listOf(listOf(1, 2), listOf(3, -1), listOf(4))) {
        batch.forEach { item ->
            if (item < 0) break@outer      // breaks the OUTER loop through an inline lambda
            println("processing $item")
        }
    }
}
```

Explicit backing fields (preview; needs `-Xexplicit-backing-fields`) — kills the `_state`/`state` two-property idiom:

```kotlin
// Preview syntax — does not compile on stable 2.1 without the flag:
// class VM {
//     val state: StateFlow<UiState>
//         field = MutableStateFlow(UiState.Loading)   // internally MutableStateFlow
//     fun load() { state.value = UiState.Ready }      // inside: mutable; outside: StateFlow
// }
```

Context parameters (2.2 preview, replaces context receivers; needs `-Xcontext-parameters`):

```kotlin
// Preview syntax:
// interface Logger { fun log(msg: String) }
// context(logger: Logger)
// fun process() { logger.log("processing...") }
```

### 10.3 Roadmap Awareness: Rich Errors / Union Types for Errors

Direction announced (KotlinConf 2025): error unions like `User | NetworkError` — constrained unions (errors only) to make Result/Either patterns first-class with compiler-enforced handling. Status: design/roadmap — teach as "where the language is going", not as available syntax.

### 10.4 Multiplatform (Awareness Level)

```kotlin
// commonMain — does not compile in a single-target scratch file; needs a KMP module:
// expect fun platformName(): String
//
// androidMain:
// actual fun platformName() = "Android ${Build.VERSION.SDK_INT}"
//
// iosMain:
// actual fun platformName() = UIDevice.currentDevice.systemName
```

- `expect`/`actual`: compile-time contract; every target must provide `actual`. Modern guidance: prefer **interfaces + DI** for large abstractions; expect/actual for small leaf functions.
- Kotlin/Native memory model: the old strict model (frozen objects, `InvalidMutabilityException`) is **gone** since 1.7.20 — new MM is a tracing GC; shared mutable state allowed (same discipline as JVM).
- Kotlin/JS compiles via IR; Kotlin/Wasm is the newer frontier.

### 10.5 Tooling & Build

```kotlin
// build.gradle.kts
// kotlin {
//     explicitApi()                       // library mode: explicit visibility + return types
//     compilerOptions {
//         freeCompilerArgs.addAll("-Xjvm-default=all", "-progressive")
//         allWarningsAsErrors.set(true)
//     }
// }
```

```toml
# gradle/libs.versions.toml — version catalog
[versions]
kotlin = "2.1.20"
coroutines = "1.10.1"
[libraries]
kotlinx-coroutines = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
```

detekt custom rule (needs `detekt-api` on the classpath of a detekt-rules module):

```kotlin
// import io.gitlab.arturbosch.detekt.api.*
// import org.jetbrains.kotlin.psi.KtCallExpression
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
```

Closes the loop with §8.1 — encode the lessons of this deck as automated rules.

---

## Dependency Cheat Sheet for the Snippets

| Sections | Needs |
|---|---|
| 1–5 (except 4.4 flow, 5.3 persistent) | stdlib only |
| 4.4, 6.x, 7.2, 8.1 | `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1` |
| 5.3 persistent | `org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.8` |
| 9.4 | `org.jetbrains.kotlin:kotlin-reflect` |
| 9.6 serialization, 10.2 previews, 10.5 detekt | plugin/flag noted inline, kept commented |

---

## Quick Self-Test (one question per block)

1. Why does reading an `open val` in a base-class `init` block print the default value (0/null)?
2. When does a non-capturing lambda allocate? A capturing one?
3. `lateinit` can't hold an `Int` — what's the bytecode-level reason?
4. Show how to mutate a `val cache: List<Item>` from outside the class — and two defenses.
5. For a 5-element list with `.filter.map`, which is faster: collection or sequence? Why?
6. `async` inside a normal scope throws — when does the parent find out: at `await()` or immediately?
7. Why can't you suspend inside a `synchronized` block?
8. What does `runCatching` break in structured concurrency?
9. Why does Gson need ProGuard keep rules but kotlinx.serialization doesn't?
10. What problem do explicit backing fields solve?

