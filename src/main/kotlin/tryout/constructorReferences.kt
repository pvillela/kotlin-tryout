package tryout

import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters


data class Foo(val i: Int) {
    constructor() : this(0)
}

fun function(factory: (Int) -> Foo, param: Int): Foo {
    val x: Foo = factory(param)
    return x
}


fun main() {
    val x = function(::Foo, 1)
    println(x)

    val c = Foo::class.primaryConstructor!!
    val params = c.valueParameters
    params.forEach(::println)
}
