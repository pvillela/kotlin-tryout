package tryout


fun main() {
    val boundedExponentialBackoff = { initialValue: Long, growthRate: Double, bound: Long ->
        var state = initialValue
        {
            val oldState = state
            if (state < bound) state = (state * growthRate).toLong()
            if (state > bound) state = bound
            oldState
        }
    }(100, 2.0, 1000)

    println(boundedExponentialBackoff())
    println(boundedExponentialBackoff())
    println(boundedExponentialBackoff())
    println(boundedExponentialBackoff())
    println(boundedExponentialBackoff())
    println(boundedExponentialBackoff())
}
