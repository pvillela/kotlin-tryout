/*
 * From https://medium.com/@jerzy.chalupski/emulating-self-types-in-kotlin-d64fe8ea2e62
 */

package tryout

import java.lang.Math.PI


object SelfTypes {

    open class SimpleCalculator {
        var result: Double = 0.0
            internal set
    }

    class ScientificCalculator : SimpleCalculator()

    fun <T : SimpleCalculator> T.add(value: Double): T = apply { result += value }

    fun ScientificCalculator.sin() = apply { result = kotlin.math.sin(result) }

    val calc: ScientificCalculator = ScientificCalculator()
            .add(PI)
            .sin()
    val result = calc.result
}


fun main() {
    println(SelfTypes.result)
}
