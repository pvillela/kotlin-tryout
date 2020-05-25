package tryout

import arch.util.fatal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

object CoroutineCancellationWithSideEffects {

    suspend fun <T> cancellationWithSideEffectsPattern(
            executionScope: CoroutineScope,
            internal: suspend CoroutineScope.() -> T,
            exceptionSideEffects: suspend CoroutineScope.(Exception) -> Unit,
            finalSideEffects: () -> Unit
    ): Pair<Deferred<T>, Deferred<T>> = with(executionScope) {
        // Shield executionScope from exceptions in internal.
        val internalJob: Deferred<T> = async { internal() }

        val externalJob = async {
            try {
                internalJob.await()
            } catch (e: Exception) {
                if (fatal(e)) throw e
                withContext<T>(NonCancellable) {
                    exceptionSideEffects(e)
                    internalJob.cancelAndJoin()
                    throw e
                }
            } finally {
                finalSideEffects()
            }
            internal()
        }

        return@with Pair(internalJob, externalJob)
    }

    suspend fun CoroutineScope.exceptionSideEffects(e: Exception) {
        println("%%% exceptionSideEffects: internalJob cancelled due to $e ... %%%")
        delay(100)
        println("%%% ... finished processing exception side-effects. %%%")
    }

    fun finalSideEffects() {
        println("%%% final side-effects %%%")
    }

    suspend fun CoroutineScope.internalNormal(): Int =
            try {
                delay(1000)
                42
            } catch (e: CancellationException) {
                println("%%% internalJob cancelled %%%")
                delay(1000)
                println("internalJob doing stuff during cancellation.")
                99
            }

    suspend fun CoroutineScope.internalAbnormal(): Int {
        delay(1000)
        throw Exception("My internal job exception")
    }

    suspend fun CoroutineScope.exampleWithNormalTerminationOfInner(): Pair<Deferred<Int>, Deferred<Int>> =
            tryout.CoroutineCancellationWithSideEffects.cancellationWithSideEffectsPattern(
                    executionScope = this,
                    internal = { internalNormal() },
                    exceptionSideEffects = { exceptionSideEffects(it) },
                    finalSideEffects = { finalSideEffects() }
            )

    suspend fun CoroutineScope.exampleWithAbnormalTerminationOfInner(): Pair<Deferred<Int>, Deferred<Int>> =
            tryout.CoroutineCancellationWithSideEffects.cancellationWithSideEffectsPattern(
                    executionScope = this,
                    internal = { internalAbnormal() },
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

        runBlocking {
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

        runBlocking {
            println("\n********************")
            println("""
                    When internalJob is cancelled, externalJob will propagate the cancellation 
                    exception that terminated internalJob and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
            val (internalJob, externalJob) = exampleWithNormalTerminationOfInner()
            yield() // Required for the examples using delay with runBlocking.
            internalJob.cancel("Cancellation from the inside", ArithmeticException("Cancellation from the inside"))
            safeAwait("externalJob:", externalJob)
            safeAwait("internalJob:", internalJob)
        }

        runBlocking {
            println("\n********************")
            println("""
                    When internalJob terminates abnormally, externalJob terminates with the same
                    exception and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
        }

        runBlocking {
            println("\n********************")
            println("""
                    When externalJob is cancelled while internalJob is active, internalJob is cancelled
                    and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
        }

        runBlocking {
            println("\n********************")
            println("""
                    When externalJob is cancelled while handling internalJob's termination due to
                    cancellation, externalJob will propagate the cancellation exception that
                    terminated internalJob and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
        }

        runBlocking {
            println("\n********************")
            println("""
                    When externalJob is cancelled while handling internalJob's termination due to
                    failure, externalJob will propagate the exception that
                    terminated internalJob and all cancellation side-effects are executed.
                    ---
                    """.trimIndent())
        }
    }
}
