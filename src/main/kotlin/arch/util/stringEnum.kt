package foa3k.arch.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue


/**
 * Provides, together with [StringEnumControl], support for enums that are based on [String] rather than [Int].
 * Concrete string enum classes inherit from this class. String enum classes are automatically serialized
 * and deserialized by Jackson. See [StringEnumExample].
 */
abstract class StringEnum<T : StringEnum<T>>(
        open val v: String,
        private val control: StringEnumControl<T>
) {
    init {
        require(control.isValid((v))) {
            "$v is not a valid value for ${StringEnum::class.simpleName} subtype ${this::class.simpleName}" }
    }

    override fun toString(): String = "${this::class.simpleName}($v)"
}

/**
 * Provides, together with [StringEnum], support for enums that are based on [String] rather than [Int].
 * This class controls the valid string values in its corresponding [StringEnum] subtype [T].
 * Concrete string enum classes should contain a companion object that inherits from this class.
 * See [StringEnumExample].
 */
abstract class StringEnumControl<T : StringEnum<T>>(
        val makeEnum: (String) -> T
) {
    private val validVs: MutableSet<String> = mutableSetOf()

    /**
     * Creates and returns a [T] instance based on [v] after registering [v] as a valid string value.
     */
    fun makeAndRegister(v: String): T {
        validVs.add(v)
        return makeEnum(v)
    }

    /**
     * Checks whether [v] is a valid underlying string value for [T].
     */
    fun isValid(v: String): Boolean = v in validVs

    /**
     * Returns a list of all instance values of [T].
     */
    fun values(): List<T> = validVs.map(makeEnum)

    /**
     * Returns a list of all string values of [T] instances.
     */
    fun stringValues(): List<String> = validVs.toList()
}

enum class Bar {
    FOO,
    BAR
}

enum class Baz(val v: Int) {
    FOO(1),
    BAR(99)
}


/**
 * Example of definition and usage of a string enum class based on [StringEnum]. Includes demonstration of
 * Jackson serialization and deserialization.
 */
object StringEnumExample {
    class Foo(v: String) : StringEnum<Foo>(v, Companion) {
        companion object : StringEnumControl<Foo>({ Foo(it) }) {
            val FOO = makeAndRegister("FOO")
            val BAR = makeAndRegister("BAR")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val objectMapper = ObjectMapper()
                .registerModule(KotlinModule())

        println(Foo.FOO)
        println(Foo.isValid("FOO"))
        println(Foo("FOO"))

        println(Foo.BAR)
        println(Foo.isValid("BAR"))
        println(Foo("BAR"))

        println(Foo.isValid("boo"))
        try {
            println(Foo("boo"))
        } catch (e: Exception) {
            println(e)
        }

        println(Foo.values())
        println(Foo.stringValues())

        val fooSer = objectMapper.writeValueAsString(Foo.FOO)
        println(fooSer)
        val fooDes = objectMapper.readValue<Foo>(fooSer)
        println(fooDes)

        println(Bar.FOO)
        val barFooSer = objectMapper.writeValueAsString(Bar.FOO)
        println(barFooSer)
        val barFooDes = objectMapper.readValue<Bar>(barFooSer)
        println(barFooDes)

        println()
        println(Baz.FOO)
        val bazFooSer = objectMapper.writeValueAsString(Baz.FOO)
        println(bazFooSer)
        val bazFooDes = objectMapper.readValue<Baz>(bazFooSer)
        println(bazFooDes)

        println()
        val bazFooSerNum = "0"
        println(bazFooSerNum)
        val bazFooDesNum = objectMapper.readValue<Baz>(bazFooSerNum)
        println(bazFooDesNum)

        println()
        val barFooSerStr = "\"FOO\""
        println(barFooSerStr)
        val barFooDesStr = objectMapper.readValue<Bar>(barFooSerStr)
        println(barFooDesStr)

        println()
        val barFooSerStr1 = "\"BAZ\""
        println(barFooSerStr1)
        val barFooDesStr1 = objectMapper.readValue<Bar>(barFooSerStr1)
        println(barFooDesStr1)
    }
}
