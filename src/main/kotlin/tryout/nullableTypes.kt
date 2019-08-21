package tryout


/*
 * This file demonstrates the behavior of T::class.java for native and nullable types.
 */

inline fun <reified T> printCls() {
    println(T::class.java)
}


fun <T> printCls(cls: Class<T>) {
    println(cls)
}

inline fun <reified T1> javaClassEquals(cls: Any) =
        T1::class.java == cls


inline fun <reified T> javaType(): Class<T> =
        T::class.java


inline fun <reified T> javaTypeA(): Any =
        T::class.java

fun main() {
    printCls<Int>()
    printCls<Int?>()

    println()
    println("${Int::class.java}")
    printCls(Int::class.java)
    printCls(Int::class.javaObjectType)

    println()
    println(javaType<Int>())
    println(javaType<Int?>())

    println()
    println(javaClassEquals<Int>(Int::class.java))
    println(javaClassEquals<Int>(Int::class.javaObjectType))
    println(javaClassEquals<Int>(javaType<Int>()))
    println(javaClassEquals<Int>(javaTypeA<Int>()))
}
