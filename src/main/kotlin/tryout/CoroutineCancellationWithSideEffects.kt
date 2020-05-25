package tryout

import arch.util.fatal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

object CoroutineCancellationWithSideEffects {

    suspend fun <T> cancellationWithSideEffectsPattern(
            executionScope: CoroutineScope,
            block: suspend CoroutineScope.() -> T,
            exceptionSideEffects: suspend CoroutineScope.(Exception) -> Unit,
            finalSideEffects: () -> Unit
    ): Pair<Deferred<T>, Deferred<T>> = with(executionScope) {
        // Shield executionScope from exceptions in block.
        val internalJob: Deferred<T> = async(SupervisorJob()) { block() }

        val externalJob = async {
            try {
                internalJob.await()
            } catch (e: Exception) {
                if (fatal(e)) throw e
                withContext<T>(NonCancellable) {
                    exceptionSideEffects(e)
                    internalJob.cancel("Cancelled externally", e)
                    internalJob.join()
                    throw e
                }
            } finally {
                finalSideEffects()
            }
        }

        return@with Pair(internalJob, externalJob)
    }

    suspend fun CoroutineScope.internalNormal(): Int =
            try {
                delay(400)
                42
            } catch (e: CancellationException) {
                println("@@@ internalJob cancelled @@@")
                delay(50)
                println("internalJob doing stuff during cancellation.")
                99
            }

    suspend fun CoroutineScope.internalAbnormal(): Int {
        delay(50)
        throw Exception("My internal job exception")
    }

    suspend fun CoroutineScope.exceptionSideEffects(e: Exception) {
        println("%%% exceptionSideEffects: internalJob cancelled due to $e ... %%%")
        delay(300)
        println("%%% ... finished processing exception side-effects. %%%")
    }

    fun finalSideEffects() {
        println("%%% final side-effects %%%")
    }

    suspend fun CoroutineScope.exampleWithNormalTerminationOfInner(): Pair<Deferred<Int>, Deferred<Int>> =
            tryout.CoroutineCancellationWithSideEffects.cancellationWithSideEffectsPattern(
                    executionScope = this,
                    block = { internalNormal() },
                    exceptionSideEffects = { exceptionSideEffects(it) },
                    finalSideEffects = { finalSideEffects() }
            )

    suspend fun CoroutineScope.exampleWithAbnormalTerminationOfInner(): Pair<Deferred<Int>, Deferred<Int>> =
            tryout.CoroutineCancellationWithSideEffects.cancellationWithSideEffectsPattern(
                    executionScope = this,
                    block = { internalAbnormal() },
                    exceptionSideEffects = { exceptionSideEffects(it) },
                    finalSideEffects = { finalSideEffects() }
            )

    suspend fun safeAwait(label: String, result: Deferred<*>) {
        try {
            println("$label: result.await()=${result.await()}")
        } catch (e: Exception) {
            println("$label: caught outside: $e, cause=${e.cause}")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking(Dispatchers.Default) {
            println("\n********************")
            println("""
                    When internalJob terminates normally, awaiting on externalJob produces the same
                    result as awaiting on internalJob plus the final side-effects.
                    ---
                    """.trimIndent())
            val (internalJob, externalJob) = exampleWithNormalTerminationOfInner()
            yield() // Required for the examples using delay with runBlocking.
            safeAwait("externalJob:", externalJob)
            safeAwait("internalJob:", internalJob)
        }

        try {
            runBlocking(Dispatchers.Default) {
                println("\n********************")
                println("""
                    When internalJob terminates abnormally, externalJob terminates with 
                    internalJob's exception and all cancellation side-effects are executed. 
                    executionScope also terminates with the same exception.
                    ---
                    """.trimIndent())
                val (internalJob, externalJob) = exampleWithAbnormalTerminationOfInner()
                yield() // Required for the examples using delay with runBlocking.
                safeAwait("externalJob:", externalJob)
                safeAwait("internalJob:", internalJob)
            }
        } catch (e: Exception) {
            println("Parent scope terminated with $e, cause=${e.cause}")
        }

        runBlocking(Dispatchers.Default) {
            println("\n********************")
            println("""
                    When internalJob is cancelled, externalJob will propagate the cancellation 
                    exception that terminated internalJob and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
            val (internalJob, externalJob) = exampleWithNormalTerminationOfInner()
            yield() // Required for the examples using delay with runBlocking.
            internalJob.cancel("internalJob cancelled", ArithmeticException("internalJob cancelled"))
            yield()
            safeAwait("externalJob:", externalJob)
            safeAwait("internalJob:", internalJob)
        }

        runBlocking(Dispatchers.Default) {
            println("\n********************")
            println("""
                    When externalJob is cancelled while internalJob is active, internalJob is cancelled
                    and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
            val (internalJob, externalJob) = exampleWithNormalTerminationOfInner()
            yield() // Required for the examples using delay with runBlocking.
            externalJob.cancel("externalJob cancelled", ArithmeticException("externalJob cancelled"))
            yield()
            safeAwait("externalJob:", externalJob)
            safeAwait("internalJob:", internalJob)
        }

        runBlocking(Dispatchers.Default) {
            println("\n********************")
            println("""
                    When externalJob is cancelled while handling internalJob's termination due to
                    cancellation, externalJob will propagate internalJob's cancellation exception
                    and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
            val (internalJob, externalJob) = exampleWithNormalTerminationOfInner()
            yield() // Required for the examples using delay with runBlocking.
            internalJob.cancel("internalJob cancelled", ArithmeticException("internalJob cancelled"))
            delay(550)
            externalJob.cancel("externalJob cancelled", ArithmeticException("externalJob cancelled"))
            safeAwait("externalJob:", externalJob)
            safeAwait("internalJob:", internalJob)
        }

        try {
            runBlocking(Dispatchers.Default) {
                println("\n********************")
                println("""
                    When externalJob is cancelled while handling internalJob's termination due to
                    failure, externalJob terminates with a CancellationException
                    and all cancellation side-effects are executed. 
                    executionScope terminates with the same exception as internalJob.
                    ---
                    """.trimIndent())
                val (internalJob, externalJob) = exampleWithAbnormalTerminationOfInner()
                yield() // Required for the examples using delay with runBlocking.
                delay(200)
                externalJob.cancel("externalJob cancelled", ArithmeticException("externalJob cancelled"))
                safeAwait("externalJob:", externalJob)
//                safeAwait("internalJob:", internalJob)
            }
        } catch (e: Exception) {
            println("Parent scope terminated with $e, cause=${e.cause}")
        }
    }
}
