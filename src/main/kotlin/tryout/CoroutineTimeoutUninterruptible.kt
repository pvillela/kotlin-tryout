package tryout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory


/**
 * Example of how to do a timeout on a non-interruptible coroutine.
 */
object CoroutineTimeoutUninterruptible {

    val log = LoggerFactory.getLogger(CoroutineTimeoutUninterruptible::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val timeout = 400L
        val timeCheckFrequency = 10L
        val ioDelay = 2000L
        var completedIo = false

        log.info("Launching concurrent parallel processing.")

        val job = GlobalScope.launch(Dispatchers.IO) {
            log.info("Starting I/O process.")
            Thread.sleep(ioDelay)
            log.info("Completed I/O process.")
        }

        runBlocking {
            val timeCheckInterval = timeout / timeCheckFrequency
            var remainingTime = timeout
            while (remainingTime > 0) {
                if (!job.isActive) {
                    completedIo = true
                    break
                }
                delay(timeCheckInterval)
                remainingTime -= timeCheckInterval
            }
        }

        log.info("IO completed in time? $completedIo" +
                "\n>>> At this point, the main processing would " +
                "return and leave the IO process running in the background.")

        println("\n*** Wait for IO process to complete before exiting example.\n")

        runBlocking {
            job.join()
            log.info("Completed IO processing.")
        }

        println("\n*** Completed example.")
    }
}
