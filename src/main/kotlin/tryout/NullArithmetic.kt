package tryout


object NullArithmetic {

    operator fun Int?.plus(other: Int?): Int = (this ?: 0) + (other ?: 0)
    operator fun Int?.minus(other: Int?): Int = (this ?: 0) - (other ?: 0)
    operator fun Int?.times(other: Int?): Int = (this ?: 0) * (other ?: 0)

    @JvmStatic
    fun main(args: Array<String>) {
        println(listOf(
                1 + 0,
                1 + null,
                null + 1,
                null + null,
                3 - 2,
                1 - null,
                null - 1,
                null - null,
                1 * 1,
                1 * null,
                null * 1,
                null * null
        ))
    }
}