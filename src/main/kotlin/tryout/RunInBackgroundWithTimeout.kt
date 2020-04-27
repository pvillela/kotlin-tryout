package tryout

import arch.util.Try
import arch.util.Success
import arch.util.Failure
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
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeoutException


/**
 * Example of how to do a timeout on a non-interruptible coroutine.
 */
object RunInBackgroundWithTimeout {

    val log = LoggerFactory.getLogger(RunInBackgroundWithTimeout::class.java)

    /**
     * Runs a suspend function [block] in the background with [CoroutineScope] [backgroundScope],
     * returning a [Try].
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
            backgroundDispatcher: CoroutineDispatcher,
            nonCancellableErrorBlock: ((Try<T>) -> Unit)?,
            block: suspend CoroutineScope.() -> T
    ): Try<T> {
        val channel = Channel<Try<T>>()
        val job = backgroundScope.launch(backgroundDispatcher) {
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


    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val timeout = 400L
        val ioDelay = 2000L

        log.info("Started example.")

        val result = runInBackgroundWithTimeout(
                timeout,
                GlobalScope,
                Dispatchers.IO,
                { log.info("IO took too long: result=$it.") }
        ) {
            log.info("Started IO operation.")
            Thread.sleep(ioDelay)  // This is on purpose to block the thread.
            log.info("Completed IO operation.")
            42
        }

        log.info("Result from main processing: $result" +
                "\n>>> At this point, the main processing returns " +
                "and leaves the IO process running in the background.")

        println("\n*** Wait for IO process to complete before exiting example.\n")

        delay(ioDelay + 100)

        println("\n*** Completed example.")
    }
}
