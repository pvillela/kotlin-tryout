package tryout


object EarlyReturnFromNestedRunBlocks {

    fun example(
            earlyReturn1: Boolean,
            earlyReturn2: Boolean,
            earlyReturn3: Boolean
    ) {
        println("\nexample($earlyReturn1, $earlyReturn2, $earlyReturn3)")

        val v1 = "block1".run run1@ {
            println("$this start")
            if (earlyReturn1) return@run1 1000

            val v2 = "block2".run run2@ {
                println("$this start")
                if (earlyReturn2) return@run2 2000

                val v3 = "block3".run run3@ {
                    println("$this start")
                    if (earlyReturn3) return@run2 3000
                    3
                }

                20 + v3
            }

            100 + v2
        }

        println(v1)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        example(true, false, false)
        example(false, true, false)
        example(false, false, true)
        example(false, false, false)
    }
}
