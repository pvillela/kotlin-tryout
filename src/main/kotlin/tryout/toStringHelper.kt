package tryout

import kotlin.reflect.KProperty0


fun <T : Any> T.toStringHelper(vararg props: KProperty0<*>): String {
    val builder = StringBuilder(this::class.simpleName)
    builder.append('(')
    for (prop in props) {
        val name = prop.simpleName()
        builder.append("$name=${prop()}, ")
    }
    val length = builder.length
    builder.delete(length - 2, length)
    builder.append(')')
    return builder.toString()
}

fun <T : Any> T.toStringHelper(vararg props: T.() -> Any?): String {
    val builder = StringBuilder(this::class.simpleName)
    builder.append('(')
    for (prop in props) {
        val name = prop.simpleName()
        builder.append("$name=${this.prop()}, ")
    }
    val length = builder.length
    builder.delete(length - 2, length)
    builder.append(')')
    return builder.toString()
}

fun <T : Any> T.simpleToStringHelper(vararg propValues: Any?): String {
    val builder = StringBuilder(this::class.simpleName)
    builder.append('(')
    for (value in propValues) {
        builder.append("$value, ")
    }
    val length = builder.length
    builder.delete(length - 2, length)
    builder.append(')')
    return builder.toString()
}


fun main() {
    class Foo(val foo: Int, var bar: String) {
        val baz = foo + 1
        override fun toString(): String = this.toStringHelper(Foo::foo, Foo::baz, Foo::bar)
    }

    class Bar(val foo: Int, var bar: String) {
        val baz = foo + 1
        override fun toString(): String = this.simpleToStringHelper(foo, bar, baz)
    }

    class Baz(val foo: Int, var bar: String) {
        val baz = foo + 1
        override fun toString(): String = this.toStringHelper(this::foo, this::bar, this::baz)
    }

    val foo = Foo(1, "xyz")
    val bar = Bar(9, "abc")
    val baz = Baz(42, "sdf")

    println(foo)
    println(bar)
    println(baz)
}
