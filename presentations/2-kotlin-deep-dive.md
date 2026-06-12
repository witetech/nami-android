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

# Kotlin Deep Dive

---

## Type System & Language Core

<style scoped>
li { font-size: 35px; }
</style>

- Declaration-site variance & Star projection
- Platform Types & Intersection Types
- Sealed Classes/Interfaces & Exhaustive when
- Contracts
- Operator Overloading & Convention Methods
- Destructuring Internals
- Infix Functions & Extension Function Resolution

---

## Functions & Functional Style

<style scoped>
li { font-size: 45px; }
</style>

- inline / noinline / crossinline — Bytecode Impact
- Scope Functions — When Each Actually Fits
- Local Functions & Closures & tailrec

---

## Classes, Initialization & Lifecycle

<style scoped>
li { font-size: 45px; }
</style>

- Initialization Order
- lateinit Internals
- Delegated Properties
- companion object vs Top-Level & const

---

## Equality, Immutability & State

<style scoped>
li { font-size: 45px; }
</style>

- == vs === & equals/hashCode Contracts
- val ≠ Immutable
- The Cast Attack: List → MutableList
- Backing Properties Beyond Flow

---

## Collections & Performance

<style scoped>
li { font-size: 45px; }
</style>

- Sequences vs Collections
- Primitive Arrays
- ArrayDeque & Persistent Collections
- Lesser-Known Operators

---

## Coroutines

<style scoped>
li { font-size: 35px; }
</style>

- Suspend Functions & Builders
- Structured Concurrency & Job Hierarchy
- Dispatchers & Context Switching
- Cooperative Cancellation
- Timeouts & Retry
- Exception Handling

---

## Coroutines: Flow & Beyond

<style scoped>
li { font-size: 35px; }
</style>

- Flow Basics: Builders & Operators
- Flow: Cold/Hot & Backpressure
- Bridging Callback APIs
- Mutex & Semaphore vs synchronized
- Coroutine Internals: CPS & State Machines

---

## Error Modeling

<style scoped>
li { font-size: 45px; }
</style>

- Result&lt;T&gt; & the runCatching Trap
- Checked Exceptions Interop & @Throws
- Typed Errors: Either-Style

---

## Modern Kotlin & Ecosystem

<style scoped>
li { font-size: 45px; }
</style>

- K2 Compiler & Smarter Smart Casts
- New Language Features (2.2+)
- Multiplatform Awareness
