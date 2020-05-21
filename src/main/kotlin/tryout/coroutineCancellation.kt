package tryout

import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory


fun main() {
    val log = LoggerFactory.getLogger("coroutineCancellation")

    runBlocking {
        println("\n********************")
        println("""When a Deferred is cancelled, awaiting on it will throw CancellationException. The cause 
                |parameter of cancel will appear as the cause of the thrown CancellationException."""
                .trimMargin())

        val pseudoJob = async {
            log.info("Started async, about to pause ...")
            delay(1000)
            log.info("... completed pausing in async.")
        }

        // Give pseudoJob a chance to launch
        delay(50)
        pseudoJob.cancel("Cancelled pseudoJob", IllegalArgumentException("I want to see this when I await"))

        try {
            pseudoJob.await()
        } catch (e: Exception) {
            println("e: $e")
            println("cause: ${e.cause}")
        }
    }

    runBlocking {
        println("\n********************")
        println("""Cancelling a child doesn't cancel the parent.""")

        lateinit var childJob: Job
        val parentJob = async {
            childJob = launch {
                log.info("Started launch, about to pause ...")
                delay(1000)
                log.info("... completed pausing in launch.")
            }
            val d = async {
                delay(100)
                42
            }
            d.await()
        }

        // Give childJob a chance to get initialized
        delay(50)
        childJob.cancel("Cancelled childJob")

        try {
            val result = parentJob.await()
            println(result)
        } catch (e: Exception) {
            println("e: $e")
            println("cause: ${e.cause}")
        }
    }

    runBlocking {
        println("\n********************")
        println("""Again, cancelling a child doesn't cancel the parent.""")

        val parentJob = async {
            launch {
                log.info("Started launch, about to pause ...")
                withTimeout(200) {
                    delay(1000)
                }
                log.info("... completed pausing in launch.")
            }
            val d = async {
                delay(100)
                42
            }
            d.await()
        }

        try {
            val result = parentJob.await()
            println(result)
        } catch (e: Exception) {
            println("e: $e")
            println("cause: ${e.cause}")
        }
    }

    runBlocking {
        println("\n********************")
        println("""Cancelling a cancelled Deferred does nothing.""")

        val pseudoJob = async {
            delay(1000)
            42
        }

        try {
            pseudoJob.cancel()
            val result = pseudoJob.await()
            println(result)
        } catch (e: Exception) {
            println("e: $e")
            println("cause: ${e.cause}")
        }

        try {
            pseudoJob.cancel() // second cancel
            pseudoJob.cancel() // third cancel
            val result = pseudoJob.await()
            println(result)
        } catch (e: Exception) {
            println("e: $e")
            println("cause: ${e.cause}")
        }
    }

    runBlocking {
        println("\n********************")
        println("""Cancelling a completed Deferred does nothing.""")

        val pseudoJob = async {
            delay(100)
            42
        }

        pseudoJob.join()

        try {
            pseudoJob.cancel()
            val result = pseudoJob.await()
            println(result)
        } catch (e: Exception) {
            println("e: $e")
            println("cause: ${e.cause}")
        }

        try {
            pseudoJob.cancel() // second cancel
            pseudoJob.cancel() // third cancel
            val result = pseudoJob.await()
            println(result)
        } catch (e: Exception) {
            println("e: $e")
            println("cause: ${e.cause}")
        }
    }
}
