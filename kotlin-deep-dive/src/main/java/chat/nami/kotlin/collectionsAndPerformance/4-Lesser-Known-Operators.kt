package chat.nami.kotlin.collectionsAndPerformance

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
