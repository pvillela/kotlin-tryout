package tryout


object FunctionTypeUnion {

    @Suppress("UNCHECKED_CAST")
    fun <TFunIn, TFunOut> typeSwitch(
            key: Int,
            f1: TFunIn,
            f2: TFunIn
    ): TFunOut =
            when (key) {
                1 -> f1
                2 -> f2
                else -> throw java.lang.IllegalArgumentException("Unsupported type.")
            } as TFunOut

    interface Foo {
        val x: Int
    }

    interface Foo1 : Foo {
        val y: String
    }

    interface Foo2 : Foo {
        val z: Int
    }

    data class FooU(
            override val x: Int,
            override val y: String,
            override val z: Int
    ) : Foo1, Foo2

    fun fun1(foo: Foo1) { println("fun1: $foo") }
    fun fun2(foo: Foo2) { println("fun2: $foo") }

    fun bar(key: Int, foo: Foo) {
        val funU = typeSwitch<FT_in, FT_out>(key, ::fun1, ::fun2)
        funU(foo)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val fooU = FooU(1, "2", 3)
        val foo1 = object : Foo1 {
            override val x: Int = 10
            override val y: String = "20"
        }

        bar(1, fooU)
        bar(2, fooU)
        bar(1, foo1)
        bar(2, foo1)
    }
}

typealias FT<TFooU> = (TFooU) -> Unit
typealias FT_in = FT<FunctionTypeUnion.FooU>
typealias FT_out = FT<FunctionTypeUnion.Foo>
