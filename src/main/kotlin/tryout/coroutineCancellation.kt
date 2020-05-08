package tryout

import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory


fun main() = runBlocking {
    val log = LoggerFactory.getLogger("coroutineCancellation")

    val pseudoJob = async {
        log.info("Started async, about to pause ...")
        delay(1000)
        log.info("... completed pausing in async.")
    }

    pseudoJob.cancel("Cancelled pseudoJob", IllegalArgumentException("I want to see this when I await"))

    try {
        pseudoJob.await()
    } catch (e: Exception) {
        println("e: $e")
        println("cause: ${e.cause}")
    }
}
