package tryout

import com.natpryce.konfig.*
import java.util.*


private val map = mapOf(
        "a" to "def",
        "b" to 2,
        "pqp" to "3",
        "foo.bar" to "xxx",
        "foo.baz" to "42",
        "bar" to mapOf("x" to "y")
)

private val propsD = Properties().apply {
    putAll(map)
}

private val cp = ConfigurationProperties(propsD)

private val a: String by map
private val b: Int by map
private val bar: Map<String, String> by map

private val pqp by stringType
//val fooBar by stringType

private object foo : PropertyGroup() {
    val bar by stringType
    val baz by intType
}


fun main(args: Array<String>) {
    println(a)
    println(b)
    println(cp[pqp])
    println(cp[foo.bar])
//    println(cp[fooBar])
    println(cp[foo.baz])
}
