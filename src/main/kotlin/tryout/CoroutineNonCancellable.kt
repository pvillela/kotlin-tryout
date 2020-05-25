package tryout

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield


object CoroutineNonCancellable {

    suspend fun f0(): Unit = supervisorScope {
        println("\n********************")
        println("""
                    A NonCancellable block in a cancelled coroutine will execute when the coroutine
                    is cancelled.
                    ---
                    """.trimIndent())

        suspend fun foo(): Int {
            delay(100)
            return 42
        }

        val job1 = launch {
            println("Beginning of job1")
            withContext(NonCancellable) {
                val a = 1
                val b = foo()
                val c = 2
                println("job1 calculation: ${a + b + c}")
            }
            println("job1 after NonCancellable")
            delay(1)
            println("End of job1")
        }

        val job2 = launch {
            println("Beginning of job2")
            val a = 1
            val b = foo()
            val c = 2
            println("job2 calculation: ${a + b + c}")
            println("End of job2")
        }

        yield()
        job1.cancel()
        job2.cancel()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            f0()
        }
    }
}
