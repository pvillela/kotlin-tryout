package tryout.polymorphicdata

import java.lang.IllegalArgumentException


object NarrowingFunctions1 {

    interface Foo {
        val mediaType: String
        val commonA: Int
        var commonB: String

        fun toData(): FooData
    }


    interface Foo1 : Foo {
        var notCommon1: Int
        var pseudoCommon: Int

        companion object
    }

    class Foo1Impl(private val data: FooData) : FooU(data), Foo1 {
        override var notCommon1: Int
            get() = notNull(data.notCommon1)
            set(x) = run { data.notCommon1 = x }
        override var pseudoCommon: Int
            get() = notNull(data.pseudoCommon)
            set(x) = run { data.pseudoCommon = x }
    }

    operator fun Foo1.Companion.invoke(
            commonA: Int,
            commonB: String,
            notCommon1: Int
    ): Foo1 =
            Foo1Impl(FooData(
                    mediaType = Foo1::class.java.simpleName,
                    commonA = commonA,
                    commonB = commonB,
                    notCommon1 = notCommon1
            ))

    operator fun Foo1.Companion.invoke(data: FooData): Foo1 = Foo1Impl(data)

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

    interface Foo2 : Foo {
        val notCommon2: String
        var pseudoCommon: Int?

        companion object
    }

    class Foo2Impl(private val data: FooData) : FooU(data), Foo2 {
        override val notCommon2: String
            get() = notNull(data.notCommon2)
        override var pseudoCommon: Int?
            get() = data.pseudoCommon
            set(x) = run { data.pseudoCommon = x }
    }

    operator fun Foo2.Companion.invoke(
            commonA: Int,
            commonB: String,
            notCommon2: String
    ): Foo2 =
            Foo2Impl(FooData(
                    mediaType = Foo2::class.java.simpleName,
                    commonA = commonA,
                    commonB = commonB,
                    notCommon2 = notCommon2
            ))

    operator fun Foo2.Companion.invoke(data: FooData): Foo2 = Foo2Impl(data)

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


    abstract class FooU(private val data: FooData) : Foo by data {
        override fun toData() = data
        protected fun <T : Any> notNull(prop: T?): T {
            check(prop != null) { "Property not defined for subtype ${data.mediaType}" }
            return prop
        }
    }


    data class FooData(
            override val mediaType: String,
            override val commonA: Int,
            override var commonB: String,
            var notCommon1: Int? = null,
            val notCommon2: String? = null,
            var pseudoCommon: Int? = null
    ) : Foo {
        override fun toData() = this

        @Suppress("UNCHECKED_CAST")
        fun <T : Foo> toType(cls: Class<T>): T = when (cls) {
            Foo1::class.java -> Foo1(this) as T
            Foo2::class.java -> Foo2(this) as T
            else -> throw IllegalArgumentException("Unsupported type $cls")
        }
    }
}
