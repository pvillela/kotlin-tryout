package tryout

import propsview.arch.PropsView
import java.util.Properties


val propsV: Properties = Properties().apply { putAll(mapOf(
        "pv.a" to "pva",
        "pv.b" to 42,
        "xyz" to "mriganka",
        "abc" to "bar"
))}


object propsViewExample1 : PropsView(propsV, "pv") {
    val a: String by this
    val b: Int by this
    val c: String by this
}


data class PropsViewExample1(val dummy: Unit) : PropsView(propsV, "pv") {
    val a: String by this
    val b: Int by this
    val c: String by this
}

val prosViewExample1a = PropsViewExample1(Unit)


object propsViewExample2 : PropsView(propsV) {
    val xyz: String by this
    val abc: Int by this
}


fun main(args: Array<String>) {
    println(propsViewExample1.a)
    println(propsViewExample1.b)
    try {propsViewExample1.c} catch(e: Exception) {println(e)}
    println(propsViewExample2.xyz)
    try {propsViewExample2.abc} catch(e: Exception) {println(e)}
    println(PropsView.usage)
    println(propsViewExample1)
    println(prosViewExample1a)
}
