package tryout

import arch.util.Try
import arch.util.Success
import arch.util.Failure
import arch.util.RequestContextElement
import arch.util.TryS
import arch.util.receiveWithTimeout
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.slf4j.LoggerFactory
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


/**
 * Example of how to do a timeout on a non-interruptible coroutine.
 */
object RunWithTimeout {

    val log = LoggerFactory.getLogger(RunWithTimeout::class.java)

    /**
     * Runs a suspend function [block] in the background with [CoroutineScope]
     * [backgroundScope] and [CoroutineContext] [backgroundContext] augmented with the
     * enclosing [CoroutineScope]'s [RequestContextElement], returning a [Try].
     * [backgroundContext] is typically a [CoroutineDispatcher].
     *
     * If [block] completes successfully before [timeout], this function returns a [Success].
     * Otherwise, it returns a [Failure], which can mean that either [block] threw an exception or
     * [block] had not completed at the end of the timeout period (in which case the [Failure]
     * contains a [TimeoutException].
     *
     * If [block] is cancellable, [nonCancellableErrorBlock] should be null and [block]'s
     * background job will be cancelled in case of timeout. Otherwise, [nonCancellableErrorBlock]
     * will be executed in the background when [block] completes (with success or failure) after
     * the timeout period. In this case, [nonCancellableErrorBlock] will typically log the late
     * completion or failure of [block].
     */
    suspend fun <T> runInBackgroundWithTimeout(
            timeout: Long,
            backgroundScope: CoroutineScope,
            backgroundContext: CoroutineContext,
            nonCancellableErrorBlock: ((Try<T>) -> Unit)?,
            block: suspend CoroutineScope.() -> T
    ): Try<T> {
        val requestContextElement = coroutineContext[RequestContextElement]
        val context =
                if (requestContextElement != null) backgroundContext + requestContextElement
                else backgroundContext
        val channel = Channel<Try<T>>()

        val job = backgroundScope.launch(context) {
            val result = TryS { block() }
            channel.send(result)
        }

        val result = channel.receiveWithTimeout(timeout)

        when {
            result != null -> {
                channel.close()
            }
            nonCancellableErrorBlock == null -> {
                job.cancel()
                channel.close()
            }
            else -> {
                backgroundScope.launch {
                    val res = channel.receive()
                    channel.close()
                    nonCancellableErrorBlock(res)
                }
            }
        }

        val retVal = result
                ?: Failure<T>(TimeoutException("Function running in background failed to complete in time."))

        return retVal
    }

    /**
     * Runs a suspend function [block] in the background with [CoroutineScope]
     * [backgroundScope] and [backgroundScope]'s coroutineContext augmented with the
     * enclosing [CoroutineScope]'s [RequestContextElement], returning a [Try].
     *
     * If [block] completes successfully before [timeout], this function returns a [Success].
     * Otherwise, it returns a [Failure], which can mean that either [block] threw an exception or
     * [block] had not completed at the end of the timeout period (in which case the [Failure]
     * contains a [TimeoutException].
     *
     * If [block] is cancellable, [nonCancellableErrorBlock] should be null and [block]'s
     * background job will be cancelled in case of timeout. Otherwise, [nonCancellableErrorBlock]
     * will be executed in the background when [block] completes (with success or failure) after
     * the timeout period. In this case, [nonCancellableErrorBlock] will typically log the late
     * completion or failure of [block].
     */
    suspend fun <T> runInBackgroundWithTimeout(
            timeout: Long,
            backgroundScope: CoroutineScope,
            errorBlock: (Try<T>) -> Unit,
            block: suspend CoroutineScope.() -> T
    ): Try<T> {
        val backgroundContext = backgroundScope.coroutineContext
        return runInBackgroundWithTimeout(timeout, backgroundScope, backgroundContext, errorBlock, block)
    }

    /**
     * Runs a cancellable suspend function [block], returning a [Try].
     *
     * If [block] completes successfully before [timeout], this function returns a [Success].
     * Otherwise, it returns a [Failure], which can mean that either [block] threw an exception or
     * [block] had not completed at the end of the timeout period (in which case the [Failure]
     * contains a [TimeoutException].
     */
    suspend fun <T> runCancellableWithTimeout(
            timeout: Long,
            block: suspend CoroutineScope.() -> T
    ): Try<T> {
        val result = withTimeoutOrNull(timeout) {
            block()
        }

        val retVal =
                if (result != null) Success(result)
                else Failure<T>(TimeoutException("Function running in background failed to complete in time."))

        return retVal
    }


    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val timeout = 400L
        val operationDelay = 2000L

        println("##### runCancellableWithTimeout example.")
        run {
            log.info("Started runCancellableWithTimeout example.")

            val result = runCancellableWithTimeout(
                    timeout
            ) {
                try {
                    log.info("Started cancellable operation.")
                    delay(timeout / 2)
                    log.info("Cancellable operation is still running.")
                    delay(operationDelay)
                    log.info("Completed cancellable operation.")
                    42
                } catch (e: CancellationException) {
                    log.info("Cancellable operation was cancelled.")
                    throw e
                }
            }

            log.info("Result from main processing: $result.")

            println("*** Completed cancellable example.")
        }

        println("\n##### runInBackgroundWithTimeout example.")
        run {
            log.info("Started runInBackgroundWithTimeout example.")

            val result = runInBackgroundWithTimeout(
                    timeout,
                    GlobalScope,
                    Dispatchers.IO,
                    { log.info("IO took too long: result=$it.") }
            ) {
                log.info("Started IO operation.")
                Thread.sleep(operationDelay)  // This is on purpose to block the thread.
                log.info("Completed IO operation.")
                42
            }

            log.info("Result from main processing: $result" +
                    "\n>>> At this point, the main processing returns " +
                    "and leaves the IO process running in the background.")

            println("*** Wait for IO process to complete before exiting example.")

            delay(operationDelay + 100)

            println("*** Completed IO example.")
        }
    }
}
