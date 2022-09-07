package tryout

object CloseOnVar {
    data class Foo(
        val x: Int
    )

    var foo = Foo(1)

    fun bar() {
        println(foo)
    }

    fun main() {
        bar()
        foo = Foo(42)
        bar()
    }
}

fun main() {
    CloseOnVar.main()
}
