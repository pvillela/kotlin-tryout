package tryout

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking


object CoroutineExceptions1 {

    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {
            println("\n********************")
            println("""
                    Awaiting on a Deferred produced in a coroutineScope block and handling any 
                    exceptions prevents the child coroutine from propagating the cancellation to 
                    the parent.
                    ---
                    """.trimIndent())
            try {
                val y: Deferred<Int> = coroutineScope {
                    async { throw Exception("boom") }
                }
                y.await()
            } catch (e: Exception) {
                println("Handled exception $e.")
            }

            val x = coroutineScope {
                async { 42 }
            }

            println(x.await())
        }

        try {
            runBlocking {
                println("\n********************")
                println("""
                        Creating a Deferred in a coroutineScope block without awaiting on it and 
                        handling all exceptions causes the child coroutine to propagate the
                        cancellation to the parent.
                        ---
                        """.trimIndent())
                val y: Deferred<Int> = coroutineScope {
                    async { throw Exception("boom") }
                }

                val x = coroutineScope {
                    async { 42 }
                }

                println(x.await())
            }
        } catch (e: Exception) {
            println("Parent cancelled by exception in child: $e.")
        }

        try {
            runBlocking {
                println("\n********************")
                println("""
                        Creating a Deferred, not in a coroutineScope or supervisorScope block
                        causes the child coroutine to propagate the cancellation to the parent even
                        if we wait on the Deferred and handle any exceptions.
                        ---
                        """.trimIndent())
                try {
                    val y: Deferred<Int> = run {
                        async { throw Exception("boom") }
                    }
                    y.await()
                } catch (e: Exception) {
                    println("Handled exception $e.")
                }

                val x = coroutineScope {
                    async { 42 }
                }

                println(x.await())
            }
        } catch (e: Exception) {
            println("Parent cancelled by exception in child: $e.")
        }

        try {
            runBlocking {
                println("\n********************")
                println("""
                    Creating a Deferred in a scope with SupervisorJob prevents the child coroutine from 
                    propagating the cancellation to the parent even if we don't await on the deferred and
                    handle the exception.
                    ---
                    """.trimIndent())
                val parentJob = coroutineContext[Job]!!
                val job = SupervisorJob(parentJob)
                val y: Deferred<Int> = CoroutineScope(coroutineContext + job).async { throw Exception("boom") }

                val x = coroutineScope {
                    async { 42 }
                }

                println(y)
                println(x.await())
                job.complete()
            }
        } catch (e: Exception) {
            println("Parent cancelled by exception in child: $e.")
        }
    }
}