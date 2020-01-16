package tryout.polymorphicdata


object PolymorphicVars {

    interface ElementBase

    interface ElementSub1 : ElementBase {
        val sub1: Int
    }

    interface MainBase {
        var base: ElementBase
    }

    data class MainSub1(
            override var base: ElementBase,
            val foo: Int
    ) : MainBase

    var MainSub1.narrowedBase: ElementSub1
        get() = run {
            check(base is ElementSub1) { "Property isn't of the required subtype." }
            base as ElementSub1
        }
        set(x) = run { base = x }

    fun MainSub1.safeGetBase(): ElementSub1 {
        check(base is ElementSub1) { "Property isn't of the required subtype." }
        return base as ElementSub1
    }
}
