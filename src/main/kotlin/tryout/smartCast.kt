package tryout


interface Super

class Sub : Super {
    fun foo(): Unit = println("foo")
}

fun demonstrateSmartCast(x: Super) {
    require(x is Sub)
    x.foo()
}
