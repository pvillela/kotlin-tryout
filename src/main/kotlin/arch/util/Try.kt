package arch.util


/**
 * Reifies a try-catch block into a union type with [Success] and [Failure] cases.  Simplified
 * version of Scala's Try class.
 */
sealed class Try<T> {

    companion object {

        /**
         * Executes [block] within a try-catch and captures the result as a [Try] object.
         */
        operator fun <T> invoke(block: () -> T): Try<T> =
                try {
                    Success(block())
                } catch (e: Throwable) {
                    if (fatal(e)) throw e
                    Failure(e)
                }
    }

    abstract fun isSuccess(): Boolean

    abstract fun isFailure(): Boolean

    abstract val value: T?

    abstract val exception: Throwable?
}


/**
 * Success case of [Try], representing the result of a computation that did not throw an exception.
 */
data class Success<T>(override val value: T) : Try<T>() {

    override fun isSuccess(): Boolean = true

    override fun isFailure(): Boolean = false

    override val exception: Throwable? = null
}


/**
 * Failure case of [Try], representing the result of a computation that threw an exception.
 */
data class Failure<T>(override val exception: Throwable) : Try<T>() {

    override fun isSuccess(): Boolean = false

    override fun isFailure(): Boolean = true

    override val value: T? = null
}


/**
 * Factory of Try objects for suspend blocks.  Can't be defined in Try companion object because
 * of type inferencer inability to infer the correct invoke signatures.
 */
object TryS {

    /**
     * Executes a susupend [block] within a try-catch and captures the result as a [Try] object.
     */
    operator suspend fun <T> invoke(block: suspend () -> T): Try<T> =
            try {
                Success(block())
            } catch (e: Throwable) {
                if (fatal(e)) throw e
                Failure(e)
            }
}