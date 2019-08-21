package propsview.arch

import java.util.Properties
import kotlin.reflect.KProperty


fun propertiesHash(props: Properties): String = "Properties#" + props.hashCode()


fun String.withDottedPrefix(prefix: String): String {
    val trimmedPrefix = prefix.trim()
    return if (trimmedPrefix.length == 0) this else trimmedPrefix + "." + this
}


inline fun <reified T> Properties.typedValue(key: Any): T {
    if (!this.containsKey(key)) throw IllegalArgumentException(
            "Key $key not found in ${propertiesHash(this)}")
    val valAny = this[key]
    val value: T? = valAny as? T
    return value ?: throw IllegalArgumentException(
            "Value for key $key in ${propertiesHash(this)} is not assignable to ${T::class.java}")
}


abstract class PropsView(protected val props: Properties, protected val prefix: String = "") {

    protected inline operator fun <reified T> getValue(thisRef: Any?, kProperty: KProperty<*>): T {
        val key = kProperty.name.withDottedPrefix(prefix)
        val value: T = props.typedValue(key)
        accessed(props, key)
        return value
    }

    class KeyDelegate(val props: Properties, val keyStr: String, val prefix: String) {
        inline operator fun <reified T> getValue(thisRef: Any?, kProperty: KProperty<*>): T {
            val key = keyStr.withDottedPrefix(prefix)
            val value: T = props.typedValue(key)
            accessed(props, key)
            return value
        }
    }

    fun key(keyStr: String) = KeyDelegate(props, keyStr, prefix)

    companion object {
        val usage: MutableMap<String, MutableSet<Any>> = mutableMapOf()

        fun accessed(props: Properties, key: Any) {
            val propsHash = propertiesHash(props)
            val set = usage[propsHash] ?: mutableSetOf()
            set.add(key)
            usage.put(propsHash, set)
        }
    }
}


fun mapToProperties(map: Map<*, *>): Properties {
    val props = Properties()

    fun mtp(prefix: String, map: Map<*, *>) {
        for (key in map.keys) {
            val value = map[key]
            val newPrefix = key.toString().withDottedPrefix(prefix)
            if (value is Map<*, *>) mtp(newPrefix, value)
            else props.put(newPrefix, value)
        }
    }

    mtp("", map)
    return props
}
