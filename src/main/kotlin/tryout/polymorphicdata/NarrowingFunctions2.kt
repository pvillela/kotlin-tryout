package tryout.polymorphicdata

import java.lang.IllegalArgumentException


object NarrowingFunctions2 {

    interface FooI {
        val mediaType: String
        val commonA: Int
        var commonB: String
    }


    data class Foo(
            override val mediaType: String,
            override val commonA: Int,
            override var commonB: String,
            var notCommon1: Int? = null,
            val notCommon2: String? = null,
            var pseudoCommon: Int? = null
    ) : FooI

    // This extension function may not be required in most use cases
    @Suppress("UNCHECKED_CAST")
    fun <T : FooI> Foo.toType(cls: Class<T>): T = when (cls) {
        Foo1::class.java -> Foo1(this) as T
        Foo2::class.java -> Foo2(this) as T
        else -> throw IllegalArgumentException("Unsupported type $cls")
    }


    abstract class FooNarrowed(val data: Foo) : FooI by data {
        protected fun <T : Any> safeGet(prop: T?): T {
            check(prop != null) { "Property must not be null for mediaType ${data.mediaType}" }
            return prop
        }
    }


    class Foo1(data: Foo) : FooNarrowed(data) {
        var notCommon1: Int
            get() = safeGet(data.notCommon1)
            set(x) = run { data.notCommon1 = x }
        var pseudoCommon: Int
            get() = safeGet(data.pseudoCommon)
            set(x) = run { data.pseudoCommon = x }

        companion object
    }

    operator fun Foo1.Companion.invoke(
            commonA: Int,
            commonB: String,
            notCommon1: Int
    ): Foo1 =
            Foo1(Foo(
                    mediaType = Foo1::class.java.simpleName,
                    commonA = commonA,
                    commonB = commonB,
                    notCommon1 = notCommon1
            ))

    fun Foo1.copy(
            commonA: Int = this.commonA,
            commonB: String = this.commonB,
            notCommon1: Int = this.notCommon1
    ): Foo1 =
            Foo1.Companion(
                    commonA,
                    commonB,
                    notCommon1
            )


    class Foo2(data: Foo) : FooNarrowed(data) {
        val notCommon2: String
            get() = safeGet(data.notCommon2)
        var pseudoCommon: Int?
            get() = data.pseudoCommon
            set(x) = run { data.pseudoCommon = x }

        companion object
    }

    operator fun Foo2.Companion.invoke(
            commonA: Int,
            commonB: String,
            notCommon2: String
    ): Foo2 =
            Foo2(Foo(
                    mediaType = Foo2::class.java.simpleName,
                    commonA = commonA,
                    commonB = commonB,
                    notCommon2 = notCommon2
            ))

    fun Foo2.copy(
            commonA: Int = this.commonA,
            commonB: String = this.commonB,
            notCommon2: String = this.notCommon2
    ): Foo2 =
            Foo2.Companion(
                    commonA = commonA,
                    commonB = commonB,
                    notCommon2 = notCommon2
            )
}
