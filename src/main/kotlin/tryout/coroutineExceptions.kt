package tryout

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import reactor.core.scheduler.Schedulers


// Based on
// https://kotlinlang.org/docs/reference/coroutines/exception-handling.html#exception-propagation

suspend fun f0() = coroutineScope {
    val job = GlobalScope.launch {
        println("Throwing exception from launch")
        throw IndexOutOfBoundsException("From job") // Will be printed to the console by Thread.defaultUncaughtExceptionHandler
    }
    job.join()
    println("Joined failed job")
    val deferred = GlobalScope.async {
        println("Throwing exception from async")
        throw ArithmeticException("From async") // Nothing is printed, relying on user to call await
    }
    try {
        deferred.await()
        println("Unreached")
    } catch (e: Exception) {
        println("Caught inside: $e")
    }
}

suspend fun f1(waitTime: Long) = coroutineScope {
    val job = launch {
        println("Throwing exception from launch")
        throw IndexOutOfBoundsException("From job")
    }

    val deferred: Deferred<Int> = async {
        println("Throwing exception from async")
        throw ArithmeticException(("From async"))
    }

    println("Waiting ...")
    delay(waitTime)
    println("... finished waiting $waitTime milliseconds.")

    try {
        job.join()
    } catch (e: Exception) {
        println("Joined job")
        println("Caught inside: $e")
    }

    try {
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    job to deferred
}

suspend fun f2() = coroutineScope {
    val deferred1 : Deferred<Int> = async {
        println("Throwing exception from async")
        throw ArithmeticException("From async")
    }

    val deferred2: Deferred<Int> = async {
        println("Computing deferred2 in async")
        2
    }

    deferred1 to deferred2
}


fun main() {
    runBlocking {
        println("***** f0()")
        try {
            println(f0())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1(0)")
        try {
            println(f1(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1(1000)")
        try {
            println(f1(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f2()")
        try {
            val (deferred1, deferred2) = f2()
            // The exception will be thrown just by invoking F2, without having to wait on the
            // deferreds
//            println(deferred2.await())
//            println(deferred1.await())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()
    }
}
