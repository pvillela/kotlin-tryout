package tryout.polymorphicdata

import java.lang.IllegalArgumentException


object NarrowingFunctions3 {

    interface MediaRecord {
        val mediaType: String
    }

    interface FooI : MediaRecord {
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
    ) : FooI {
        companion object
    }

    // This extension function may not be required in most use cases
    @Suppress("UNCHECKED_CAST")
    fun <T : FooI> Foo.toType(cls: Class<T>): T = when (cls) {
        Foo1::class.java -> Foo1(this) as T
        Foo2::class.java -> Foo2(this) as T
        else -> throw IllegalArgumentException("Unsupported type $cls")
    }

    fun <T : MediaRecord, U : Any> T.notNull(prop: U?): U {
        check(prop != null) { "Property must not be null for mediaType ${this.mediaType}" }
        return prop
    }


    data class Foo1(
            override val mediaType: String,
            override val commonA: Int,
            override var commonB: String,
            var notCommon1: Int,
            var pseudoCommon: Int
    ) : FooI {
        companion object
    }

    operator fun Foo1.Companion.invoke(foo: Foo): Foo1 = with(foo) {
        Foo1(
                mediaType = mediaType,
                commonA = commonA,
                commonB = commonB,
                notCommon1 = notNull(notCommon1),
                pseudoCommon = notNull(pseudoCommon)
        )
    }

    operator fun Foo.Companion.invoke(foo1: Foo1): Foo = with(foo1) {
        Foo(
                mediaType = mediaType,
                commonA = commonA,
                commonB = commonB,
                notCommon1 = notCommon1,
                pseudoCommon = pseudoCommon
        )
    }


    data class Foo2(
            override val mediaType: String,
            override val commonA: Int,
            override var commonB: String,
            var notCommon2: String,
            var pseudoCommon: Int?
    ) : FooI {
        companion object
    }

    operator fun Foo2.Companion.invoke(foo: Foo): Foo2 = with(foo) {
        Foo2(
                mediaType = mediaType,
                commonA = commonA,
                commonB = commonB,
                notCommon2 = notNull(notCommon2),
                pseudoCommon = pseudoCommon
        )
    }

    operator fun Foo.Companion.invoke(foo2: Foo2): Foo = with(foo2) {
        Foo(
                mediaType = mediaType,
                commonA = commonA,
                commonB = commonB,
                notCommon2 = notCommon2,
                pseudoCommon = pseudoCommon
        )
    }
}
