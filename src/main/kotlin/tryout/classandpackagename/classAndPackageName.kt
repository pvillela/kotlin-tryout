package tryout.classandpackagename

object Xxx {
    class A {
        class B
    }
}

/**
 * Returns the name of the package of [cls], provided that [cls] is not in the default package.
 * if [cls] is in the default package, then returns the empty string.
 */
fun packageName(cls: Class<*>): String {
    val fqn = cls.name
    val lastDot = fqn.lastIndexOf('.')
    return if (lastDot == -1) ""
    else fqn.substring(0, lastDot)
}

/**
 * Returns the name of the package of the class of [obj], provided that the class is not in the default package.
 * if the class is in the default package, then returns the empty string.
 */
fun packageName(obj: Any): String =
        packageName(obj.javaClass)

/**
 * Returns the names of the subpackages in the package path of [cls].
 * if [cls] is in the default package, then returns an empty list.
 */
fun packageNameList(cls: Class<*>): List<String> {
    val pkgName = packageName(cls)
    return if (pkgName.isNotEmpty()) pkgName.split('.')
    else emptyList()
}

/**
 * Returns the names of the subpackages in the package path of the class of [obj].
 * if the class is in the default package, then returns an empty list.
 */
fun packageNameList(obj: Any): List<String> =
        packageNameList(obj.javaClass)

fun main() {

    println("*** X ***")
    println(Xxx::class.simpleName)
    println(Xxx::class.qualifiedName)
    println(Xxx::class.java.name)
    println(Xxx.javaClass.name)
    println(packageName(Xxx::class.java))
    println(packageName(Xxx))
    println(packageNameList(Xxx::class.java))
    println(packageNameList(Xxx))
    println()

    println("*** obj ***")
    val obj = Xxx.A.B()
    println(obj::class.simpleName)
    println(obj::class.qualifiedName)
    println(obj::class.java.name)
    println(obj.javaClass.name)
    println(packageName(obj::class.java))
    println(packageName(obj))
    println(packageNameList(obj::class.java))
    println(packageNameList(obj))
    println()

    println("*** foo ***")
    val foo = object {}
    println(foo::class.simpleName)
    println(foo::class.qualifiedName)
    println(foo::class.java.name)
    println(foo.javaClass.name)
    println(packageName(foo::class.java))
    println(packageName(foo))
    println(packageNameList(foo::class.java))
    println(packageNameList(foo))
}
