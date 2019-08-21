package tryout

import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.IllegalArgumentException
import java.lang.RuntimeException


fun stackTrace(e: Throwable): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    return sw.toString()
}

fun Array<Any?>.toStringArray(): Array<Any?> {
    val strings = this.map { it.toString() }
    return strings.toTypedArray()
}

fun <T> className(cls: Class<T>) = cls.toString().drop(6)

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("tryout.logging")

    fun logError(format: String, args: Array<Any?>) = log.error(format, *args.toStringArray())
    fun logWarn(format: String, args: Array<Any?>) = log.warn(format, *args.toStringArray())
    fun logInfo(format: String, args: Array<Any?>) = log.info(format, *args.toStringArray())
    fun logDebug(format: String, args: Array<Any?>) = log.debug(format, *args.toStringArray())

    fun logThrowable(logFun: (String, Array<Any?>) -> Unit, e: Throwable) =
            logFun("Exception: {}, cause: {}", arrayOf<Any?>(e, e.cause))

    fun logStackTrace(logFun: (String, Array<Any?>) -> Unit, e: Throwable) =
            logFun("Exception: {}, cause: {}\nStack trace: {}",
                    arrayOf<Any?>(e, e.cause, stackTrace(e)))

    try {
        throw IllegalArgumentException("My IllegalArgumentException message")
    } catch(e: Throwable) {
        try {
            throw RuntimeException("My RuntimeException message", e)
        } catch(re: Throwable) {
            logThrowable(::logInfo, re)
            logStackTrace(::logInfo, re)
            println("xxxx")
            log.info("Stack trace:", re)
            println("yyyyyyy")
            println(className(re::class.java))
            println(Exception(null, null))
            println(Exception("", null))
        }
    }
}
