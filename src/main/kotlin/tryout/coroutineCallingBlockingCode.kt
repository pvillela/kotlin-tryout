/*
    Based on Elizarov's answer on https://discuss.kotlinlang.org/t/calling-blocking-code-in-coroutines/2368
 */

package tryout

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors


val processContext: CoroutineDispatcher = newFixedThreadPoolContext(4, "foo")

// Executes a blocking call (processBuilder) in the context of a Dispatcher and suspends for the
// blocking call to complete.  This is useful when processing a request that involves a blocking
// call on a multi-threaded service and there is no parallelism possible within the request.  In
// this case, the request suspends (dosen't block) and frees the main thread to service other
// requests.  When parallelism is possible and desired, use async-await instead of withContext.
// See https://stackoverflow.com/questions/50230466/kotlin-withcontext-vs-async-await.
suspend fun execute(processBuilder: ProcessBuilder): Int = withContext(processContext) {
    Thread.sleep(1000) // simulating a slow command
    processBuilder.start().waitFor()
}

fun main(args: Array<String>) {
    runBlocking {
        val processBuilder = ProcessBuilder("ls")
                .inheritIO()
        val result = execute(processBuilder)
        println("Result: $result")
    }
}
