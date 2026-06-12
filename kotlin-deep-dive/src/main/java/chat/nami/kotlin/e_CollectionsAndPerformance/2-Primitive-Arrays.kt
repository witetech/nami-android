package chat.nami.kotlin.e_CollectionsAndPerformance

fun main() {
    val a: IntArray = intArrayOf(1, 2, 3)       // int[]     — contiguous primitives
    val b: Array<Int> = arrayOf(1, 2, 3)        // Integer[] — every element boxed
    val c: List<Int> = listOf(1, 2, 3)          // ArrayList<Integer> — boxed

    println(a.sum() + b.sum() + c.sum())
}

// For hot numeric loops (audio, image, sensor data): IntArray/FloatArray = no boxing, cache-friendly.
