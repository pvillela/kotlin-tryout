package tryout


object DispatchOnNullableType {

    interface Foo

    class A : Foo

    class B : Foo

    fun dispatchBad(x: Foo?) = when (x) {
        is A? -> println("A?")
        is B? -> println("B?")
        else -> throw IllegalArgumentException("Unsupported type.")
    }

    inline fun <reified T : Foo> dispatchGood(x: T?) = when (T::class.java) {
        A::class.java -> println("A?")
        B::class.java -> println("B?")
        else -> throw IllegalArgumentException("Unsupported type.")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val a: A? = null
        val b: B? = null
        dispatchBad(a)
        dispatchBad(b)
        dispatchGood(a)
        dispatchGood(b)
    }
}
