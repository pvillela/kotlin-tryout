package arch.util

import org.slf4j.Logger
import java.io.PrintWriter
import java.io.StringWriter


/**
 * Returns true if the provided `Throwable` is to be considered fatal, false otherwise.
 * Based on NonFatal object in Scala API.
 */
fun fatal(t: Throwable): Boolean = when(t) {
    is StackOverflowError -> false // StackOverflowError is OK even though it is a VirtualMachineError
    // VirtualMachineError includes OutOfMemoryError and other fatal errors
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError -> true
    else -> false
}

/**
 * Returns a string containing the stack trace for a [java.lang.Throwable].
 */
fun stackTrace(e: Throwable): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    return sw.toString()
}

/**
 * If [e]'s cause is not null, this function returns a string that contains "; caused by ->"
 * followed by the cause class name together with its message, followed by the same information
 * for the cause's cause, * and so on recursively.  If [e]'s cause is null, it returns the empty
 * string.
 */
fun recursiveCauseMessage(e: Throwable): String {
    tailrec fun recursiveCauseMessage(e: Throwable, prefix: String): String {
        val cause = e.cause
        return if (cause == null) prefix
        else recursiveCauseMessage(cause, "$prefix; caused by -> $cause")
    }
    return recursiveCauseMessage(e, "")
}

/**
 * Returns a string containing [e]'s class name together with its message, followed, if [e]'s
 * cause is not null, by "; caused by", followed by the same information for its cause, and so
 * on recursively.
 */
fun recursiveExceptionMessage(e: Throwable): String =
        "${e}${recursiveCauseMessage(e)}"

/**
 * Logs an exception and a recursive cause message.  See [recursiveCauseMessage].
 *
 * @param logFun  function that will do the actual logging.
 * @param logger  the logger used for logging
 * @param e  the target exception.
 * @param preMessage  Optional prefix to the error message to be logged. If specified, should end with
 *  separator character(s), e.g., " -- ", to separate it from the exception information message.
 */
fun logThrowable(logFun: (Logger, () -> String) -> Unit, log: Logger, e: Throwable, preMessage: String = "") =
        logFun(log) { "${preMessage}Exception: $e${recursiveCauseMessage(e)}" }

/**
 * Logs an exception's stack trace.
 *
 * @param logFun  function that will do the actual logging.
 * @param logger  the logger used for logging
 * @param e  the target exception.
 * @param preMessage  Optional prefix to the error message to be logged. If specified, should end with
 *  separator character(s), e.g., " -- ", to separate it from the exception information message.
 */
fun logStackTrace(logFun: (Logger, () -> String) -> Unit, log: Logger, e: Throwable, preMessage: String = "") =
        logFun(log) { "${preMessage}Stack trace: ${stackTrace(e)}" }

/**
 * Logs an exception, its cause, and its stack trace.
 *
 * @param logFun  function that will do the actual logging.
 * @param logger  the logger used for logging
 * @param e  the target exception.
 * @param preMessage  Optional prefix to the error message to be logged. If specified, should end with
 *  separator character(s), e.g., " -- ", to separate it from the exception information message.
 */
fun logAll(logFun: (Logger, () -> String) -> Unit, log: Logger, e: Throwable, preMessage: String = "") =
        logFun(log) { "${preMessage}Exception: ${e}, cause: ${e.cause}\nStack trace: ${stackTrace(e)}" }
