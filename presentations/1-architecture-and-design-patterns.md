---
marp: true
theme: gaia
paginate: true
footer: "Advanced Android: From Code to Production"
---

<style>

footer {
  visibility: hidden;
}

</style>

<!-- _class: lead -->
<!-- _paginate: false -->

## Module 1

# Architecture & Design Patterns

---

## MVVM vs MVI

<style scoped>
.columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  font-size: 25px;
}
</style>

<div class="columns">
<div>

**MVVM**
- Multiple `StateFlow`s — one per state field
- View calls named ViewModel functions
- Side effects bolted on via `SharedFlow`
- Flexible, easy to adopt incrementally
- State updates are not atomic — inconsistency window between assignments

</div>
<div>

**MVI**
- Single immutable `UiState` object
- All interactions via `processIntent(Intent)`
- Side effects are a first-class `Channel<SideEffect>`
- Strict unidirectional data flow
- State updates are atomic — view never sees a half-updated state

</div>
</div>

<!--
MVVM is the Android-recommended pattern and the right default for most screens. Each piece of state lives in its own observable stream, and the View calls named functions on the ViewModel directly. The friction shows up when you have multiple related fields that must change together — you update them one at a time, leaving a window where the state is inconsistent. Side effects like showing a snackbar or navigating don't have a standard home either, so teams reach for SharedFlow or callbacks, which varies by developer.

MVI enforces discipline: the View only sends intents and renders state — it has no logic. The ViewModel's reducer always produces a new UiState from the current state plus an intent, so every transition is explicit, reproducible, and easy to unit-test. The cost is ceremony — every action needs an Intent subclass and every screen needs a contract file. Worth it on complex screens; overkill on simple ones.
-->

---

## MVVM and State Management

MVVM with `ViewModel` and `StateFlow` is the backbone of modern Android development. The ViewModel survives configuration changes and exposes UI state as a stream that composables or views observe. When combined with Unidirectional Data Flow — where events flow up and state flows down — the result is a UI that is predictable, testable, and easy to reason about. In Nami, every screen is driven by a single `UiState` data class emitted from a `StateFlow`, making the full screen state inspectable at any point.

<!--
The ViewModel is the unit of state ownership in Android. It outlives the Activity and Fragment across configuration changes, which is why it's the right place to hold UI state. StateFlow is a hot flow that always holds the latest value — perfect for representing the current screen state — and it's lifecycle-aware when collected with repeatOnLifecycle or collectAsStateWithLifecycle in Compose. Unidirectional Data Flow (UDF) is the discipline that ties it together: the View sends events upward (button taps, text input), the ViewModel processes them and updates state, and the View re-renders from that state downward. Nothing flows backwards. This makes bugs reproducible — given the same sequence of events, you always get the same state — and makes testing straightforward, since you can verify state transitions without a UI. In Nami, each screen's ViewModel exposes a single UiState sealed class, with sub-states like Loading, Success, and Error, and a separate SideEffect channel for one-shot events like navigation or showing a snackbar.
-->

---

## Clean Architecture

- **Presentation** — UI layer: Composables, ViewModels, UI state
- **Domain** — Business logic: Use cases, entities, repository interfaces
- **Data** — Infrastructure: Repository implementations, network, database
- Dependencies only point **inward** — outer layers depend on inner, never the reverse
- Domain layer has **zero Android dependencies** — plain Kotlin, fully unit-testable

<!--
Clean Architecture divides the app into three concentric layers with a strict dependency rule: code can only depend on layers closer to the center, never outward. The Presentation layer holds everything the user sees — Composables render state, ViewModels hold it. The Domain layer is the heart: it defines what the app does through use cases and entities, and declares repository interfaces it needs without caring how they're implemented. The Data layer fulfills those interfaces using Retrofit, Room, or any other infrastructure. Because Domain is pure Kotlin with no Android imports, every use case and entity is testable with plain JUnit — no emulator, no mocking of Android framework classes.
-->

---

## Use Cases

<style scoped>
.columns {
  display: grid;
  grid-template-columns: 1fr 1.6fr;
  gap: 2rem;
  font-size: 23px;
}
</style>

<div class="columns">
<div>

- Encapsulate a **single business operation** (e.g. `GetUserProfileUseCase`)
- Called by the ViewModel — keep ViewModels thin and focused on UI state
- Take repository interfaces as constructor parameters — easy to mock in tests
- One class, one responsibility — compose multiple use cases for complex flows

</div>
<div>

```kotlin
class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val resetWishlists: ResetWishlists,
    private val clearBag: ClearBag,
    // ... more dependencies
) : Logout {
    override suspend operator fun invoke(): Result<Boolean, Unit> {
        return setPushNotificationsPreference(false).also {
            clearAllSources()
        }
    }

    private suspend fun clearAllSources() {
        resetWishlists()
        userRepository.logout()
        clearBag()
        // ... clear remaining sources
    }
}
```

</div>
</div>

<!--
A use case is a named, single-purpose class that executes one business operation — GetUserProfileUseCase, SubmitOrderUseCase, LogoutUseCase. The naming convention makes the codebase self-documenting: you can read what the app does just from the use case list. The ViewModel's job shrinks to: receive UI events, call use cases, map results to UiState. Threading lives in the use case, not the ViewModel — the ViewModel just calls invoke() and collects. Because use cases depend on repository interfaces (not implementations), you swap in a fake repository in tests and exercise the full business logic in milliseconds. Returning Result<T> makes error handling explicit at every call site — no uncaught exceptions propagating up to the UI.
-->

---

## Dependency Injection

<style scoped>
li { font-size: 22px; }
</style>

- Classes declare **what they need** — not how to create it
- Dependencies are provided from the outside, making classes easy to test and replace
- **Hilt** is the standard DI framework on Android, built on top of Dagger
- `@Inject constructor` marks a class for automatic injection — no manual wiring
- `@HiltViewModel` connects the DI graph to the ViewModel lifecycle
- Scopes control lifetime: `@Singleton`, `@ActivityRetainedScoped`, `@ViewModelScoped`

<!--
Dependency Injection is the practice of supplying a class's collaborators from outside rather than having the class construct them itself. The class becomes a pure description of behavior — swap any dependency for a fake and the class still works. On Android, Hilt is the official solution: it generates the Dagger component graph at compile time, so there's no runtime reflection overhead. Annotating a constructor with @Inject is enough to make the class injectable; Hilt figures out the rest from the type graph. @HiltViewModel wires ViewModels into Hilt's scoped graph so they survive configuration changes correctly. Scopes matter because they determine how long an instance lives — a @Singleton repository is shared across the entire app, while a @ViewModelScoped helper lives and dies with its ViewModel. Getting scopes wrong is one of the most common sources of memory leaks and stale state in Android apps.
-->

---

## DI Strategies Compared

<style scoped>
table { font-size: 18px; width: 100%; }
th { background: #4A90D9; color: white; }
p { font-size: 20px; margin-bottom: 16px; }
</style>

Choose your DI approach based on app size and how early you want to catch errors — **Manual** for tiny apps, **Hilt** for production scale with compile-time safety, **Koin** for fast iteration with a clean DSL.

| Factor | Manual | Hilt / Dagger | Koin |
|---|---|---|---|
| Build speed | Fast | Slow (KSP) | Fast |
| Error detection | Manual | Compile-time | Runtime |
| Learning curve | Low | High | Low |
| Scalability | Poor | Excellent | Moderate |
| KMP support | Yes | Partial | Yes |
| Boilerplate | High | Medium | Low |

<!--
Manual DI gives you full control but falls apart as the graph grows. Hilt catches missing or mismatched bindings at compile time — you never ship a crash from a missing provider. Koin is the easiest to adopt but trades that safety for runtime resolution, so a missing binding only surfaces when that code path runs in production. For a team project like nami-android, Hilt is the right call: the build cost is a one-time investment and the compile-time guarantees pay off across the team.
-->

---

## Modularization

<style scoped>
li { font-size: 22px; }
</style>

- Split the app into independent Gradle modules with clear boundaries
- **Feature modules** own a vertical slice: UI, domain logic, and data for one feature
- **Core modules** share infrastructure: networking, database, design system, utils
- Modules depend on **interfaces, not implementations** — enforced by the build graph
- Parallel compilation: Gradle only rebuilds modules whose inputs changed
- Enables **dynamic delivery** — feature modules can be downloaded on demand

<!--
Modularization is the practice of breaking a monolithic app into a graph of Gradle modules, each with a well-defined responsibility and a minimal public API. Feature modules own everything related to a single user-facing feature — the Composables, the ViewModel, the use cases, the repository implementation — so changes stay local and don't ripple across the codebase. Core modules hold the things features share: the Retrofit client, the Room database, the design system components, logging utilities. The dependency rule mirrors Clean Architecture: feature modules depend on core modules, never on each other. This keeps the build graph a DAG and lets Gradle compile unrelated modules in parallel, which pays off dramatically as the project grows. At scale, feature modules can be delivered as Play Feature Delivery modules, downloaded only when the user navigates to that feature for the first time.
-->

