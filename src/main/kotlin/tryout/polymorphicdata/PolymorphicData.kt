package tryout.polymorphicdata


interface Foo {
    val mediaType: String
    val commonA: Int
    var commonB: String
}


interface Foo1 : Foo {
    var notCommon1: Int

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

interface Foo2 : Foo {
    val notCommon2: String

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


data class FooU(val data: FooData) : Foo by data, Foo1, Foo2 {
    override var notCommon1: Int
        get() = safeGet(data.notCommon1)
        set(x) = run { data.notCommon1 = x }
    override val notCommon2: String
        get() = safeGet(data.notCommon2)

    private fun <T : Any> safeGet(prop: T?): T {
        check(prop != null) { "Property not defined for subtype ${data.mediaType}" }
        return prop
    }
}


data class FooData(
        override val mediaType: String,
        override val commonA: Int,
        override var commonB: String,
        var notCommon1: Int? = null,
        val notCommon2: String? = null
) : Foo
