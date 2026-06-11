package chat.nami.kotlin.compilerInteropMetaprogramming

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

// Use-site targets matter on constructor properties — one declaration maps to
// param + field + getter; @param:, @field:, @get: disambiguate
// (classic: @field:Inject for field injection).
