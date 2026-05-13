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

