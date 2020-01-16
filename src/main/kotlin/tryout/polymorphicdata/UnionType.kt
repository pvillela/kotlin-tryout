package tryout.polymorphicdata

object UnionType {

    interface Foo {
        val mediaType: String
        val commonA: Int
        var commonB: String
    }


    interface Foo1S {
        var notCommon1: Int
    }

    data class Foo1D(
            override var notCommon1: Int
    ) : Foo1S

    interface Foo1 : Foo, Foo1S {
        companion object
    }

    operator fun Foo1.Companion.invoke(
            commonA: Int,
            commonB: String,
            notCommon1: Int
    ): Foo1 =
            FooU(FooData(
                    mediaType = Foo1::class.java.simpleName,
                    commonA = commonA,
                    commonB = commonB,
                    foo1D = Foo1D(notCommon1)
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


    interface Foo2S {
        val notCommon2: String
    }

    data class Foo2D(
            override val notCommon2: String
    ) : Foo2S

    interface Foo2 : Foo, Foo2S {
        companion object
    }

    operator fun Foo2.Companion.invoke(
            commonA: Int,
            commonB: String,
            notCommon2: String
    ): Foo2 =
            FooU(FooData(
                    mediaType = Foo2::class.java.simpleName,
                    commonA = commonA,
                    commonB = commonB,
                    foo2D = Foo2D(notCommon2)
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


    data class FooU(val data: FooData) :
            Foo by data,
            Foo1S by data.safeGet(data.foo1D), Foo1,
            Foo2S by data.safeGet(data.foo2D), Foo2


    data class FooData(
            override val mediaType: String,
            override val commonA: Int,
            override var commonB: String,
            val foo1D: Foo1D? = null,
            val foo2D: Foo2D? = null
    ) : Foo {

        fun <T : Any> safeGet(prop: T?): T {
            check(prop != null) { "Property not defined for subtype ${this.mediaType}" }
            return prop
        }
    }
}
