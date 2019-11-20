package tryout

import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertNull


// How to perform a computation with an expression if it is not null and return null otherwise.
fun main() {

    fun expr(i: Int) =
            if (i - 10 < 0) abs(i - 10)
            else null

    // If null: option 1 -- problematic if expr has side-effects
    run {
        fun f(x: Int): Int? =
                if (expr(x) != null) abs(expr(x)!!) * 2 else null

        assertEquals(f(7), 6)
        assertNull(f(10))
    }

    // If null: option 2
    run {
        fun f(x: Int): Int? = run {
            val y = expr(x)
            if (y != null) abs(y) * 2 else null
        }

        assertEquals(f(7), 6)
        assertNull(f(10))
    }

    // run
    run {
        fun f(x: Int): Int? =
                expr(x)?.run { this * 2 }

        assertEquals(f(7), 6)
        assertNull(f(10))
    }

    // let -- This is the best idiom
    run {
        fun f(x: Int): Int? =
                expr(x)?.let { it * 2 }

        assertEquals(f(7), 6)
        assertNull(f(10))
    }

    println("Executed successfully.")
}