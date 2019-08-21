package tryout


import tryout.TestFunValName.Cxy


private val funValNameRegex = Regex("[^ .:(]+ (([^ .:(]*\\.)*)([^ .:(]+)")


fun simpleNameFromFunValString(funValString: String): String {
    val match = funValNameRegex.find(funValString)
    return match?.groupValues?.get(3) ?: "<not_a_val_var_or_fun>"
}

fun qualifiedNameFromFunValString(funValString: String): String {
    val match = funValNameRegex.find(funValString)
    val matchResult = match?.groupValues
    if (matchResult == null) return "<not_a_val_var_or_fun>"
    val prefix = match.groupValues.get(1)
    val simpleName = match.groupValues.get(3)
    return prefix + simpleName
}

fun (() -> Any?).simpleName(): String =
        simpleNameFromFunValString(this.toString())

fun ((Nothing) -> Any?).simpleName(): String =
        simpleNameFromFunValString(this.toString())

fun ((Nothing, Nothing) -> Any?).simpleName(): String =
        simpleNameFromFunValString(this.toString())

fun (() -> Any?).qualifiedName(): String =
        qualifiedNameFromFunValString(this.toString())

fun ((Nothing) -> Any?).qualifiedName(): String =
        qualifiedNameFromFunValString(this.toString())

fun ((Nothing, Nothing) -> Any?).qualifiedName(): String =
        qualifiedNameFromFunValString(this.toString())


///////////////////
// Samples/tests

private object TestFunValName {
    class Cxy {
        fun foo(x: Int): Int = x + 1
        val bar: Int = 3
        fun baz(): Int = 4
        var fuz: String = "abc"
    }
}


private fun nullary(): String = "xyz"

private val extractor1: Cxy.() -> Int = { this.bar }

private fun Cxy.extractor2(): Int = this.bar

private fun regularFun(i: Int): Int = i + 1

private fun f(x: Int): Int = x + 1
private fun Int.g(): Int = f(this)
private val h: (Int) -> Int = Int::g
private fun k(x: Int): Int = x + 1


fun main() {

    val vFoo: (Cxy).(Int) -> Int = Cxy::foo
    val vBar: (Cxy).() -> Int = Cxy::bar
    val vBaz: (Cxy).() -> Int = Cxy::baz
    val vFuz = Cxy::fuz
    val vFuzr: (Cxy.() -> String) = Cxy::fuz
//    val vFuzw: (Cxy.(String) -> Unit) = Cxy::fuz  // doesn't compile
    val vBar1: (Cxy).() -> Int = extractor1
    val vBar2: (Cxy).() -> Int = Cxy::extractor2
    val vRegularFun: (Int) -> Int = ::regularFun

    println(funValNameRegex)

    val funValsArity0 = listOf(
            ::nullary
    )

    val funValsArity1 = listOf(
            vBar,
            vBaz,
            vFuz,
            vFuzr,
            vBar1,
            vBar2,
            vRegularFun,
            ::f,
            Int::g,
            h,
            ::k
    )

    val funValsArity2 = listOf(
            vFoo
    )

    val funValStrings = (funValsArity0 + funValsArity1 + funValsArity2).map(Any::toString)

    val funValSimpleNames0 = funValsArity0.map { it.simpleName() }
    val funValSimpleNames1 = funValsArity1.map { it.simpleName() }
    val funValSimpleNames2 = funValsArity2.map { it.simpleName() }
    val funValSimpleNames = funValSimpleNames0 + funValSimpleNames1 + funValSimpleNames2

    val funValQualifiedNames0 = funValsArity0.map { it.qualifiedName() }
    val funValQualifiedNames1 = funValsArity1.map { it.qualifiedName() }
    val funValQualifiedNames2 = funValsArity2.map { it.qualifiedName() }
    val funValQualifiedNames = funValQualifiedNames0 + funValQualifiedNames1 + funValQualifiedNames2

    println("*** toString ***")
    funValStrings.map(::println)

    println("\n*** simple name ***")
    funValSimpleNames.map(::println)

    println("\n*** qualified name ***")
    funValQualifiedNames.map(::println)
}
