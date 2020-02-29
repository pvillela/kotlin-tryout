package tryout

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope


// Based on
// https://kotlinlang.org/docs/reference/coroutines/exception-handling.html#exception-propagation

suspend fun f0() = coroutineScope {
    val job = GlobalScope.launch {
        println("Throwing exception from launch")
        throw IndexOutOfBoundsException("From job") // Will be printed to the console by Thread.defaultUncaughtExceptionHandler
    }
    job.join()
    println("Joined failed job")
    val deferred: Deferred<Int> = GlobalScope.async {
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

suspend fun f1Launch(waitTime: Long) = coroutineScope {
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
        println("Joining failed job")
        job.join()
    } catch (e: Exception) {
        println("Joined failed job")
        println("Caught inside: $e")
    }

    try {
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    job to deferred
}

suspend fun f1LaunchNoJoin(waitTime: Long) = coroutineScope {
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    job to deferred
}

suspend fun f1LaunchJoinAtEnd(waitTime: Long) = coroutineScope {
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    try {
        println("Joining job")
        job.join()
    } catch (e: Exception) {
        println("Exception thrown by job.join(): $e")
        throw e
    }

    job to deferred
}

suspend fun f1Async(waitTime: Long) = coroutineScope {
    val pseudoJob: Deferred<Int> = async {
        println("Throwing exception from pseudoLaunch")
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
        println("Joining failed pseudoJob")
        pseudoJob.join()
    } catch (e: Exception) {
        println("Joined failed pseudoJob")
        println("Caught inside: $e")
    }

    try {
        println("Awaiting on failed pseudoJob")
        pseudoJob.await()
    } catch (e: Exception) {
        println("Awaited on failed pseudoJob")
        println("Caught inside: $e")
    }

    try {
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    pseudoJob to deferred
}

suspend fun f1AsyncNoJoin(waitTime: Long) = coroutineScope {
    val pseudoJob: Deferred<Int> = async {
        println("Throwing exception from pseudoLaunch")
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    pseudoJob to deferred
}

suspend fun f1AsyncJoinAtEnd(waitTime: Long) = coroutineScope {
    val pseudoJob: Deferred<Int> = async {
        println("Throwing exception from pseudoLaunch")
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    try {
        println("Joining pseudoJob")
        pseudoJob.join()
    } catch (e: Exception) {
        println("Exception thrown by pseudoJob.join(): $e")
        throw e
    }

    try {
        println("Awaiting on pseudoJob")
        pseudoJob.await()
    } catch (e: Exception) {
        println("Exception thrown by pseudoJob.await(): $e")
        throw e
    }

    pseudoJob to deferred
}

suspend fun f1LaunchS(waitTime: Long) = supervisorScope {
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
        println("Joining failed job")
        job.join()
    } catch (e: Exception) {
        println("Joined failed job")
        println("Caught inside: $e")
    }

    try {
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    job to deferred
}

suspend fun f1LaunchNoJoinS(waitTime: Long) = supervisorScope {
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    job to deferred
}

suspend fun f1LaunchJoinAtEndS(waitTime: Long) = supervisorScope {
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    try {
        println("Joining job")
        job.join()
    } catch (e: Exception) {
        println("Exception thrown by job.join(): $e")
        throw e
    }

    job to deferred
}

suspend fun f1AsyncS(waitTime: Long) = supervisorScope {
    val pseudoJob: Deferred<Int> = async {
        println("Throwing exception from pseudoLaunch")
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
        println("Joining pseudoJob")
        pseudoJob.join()
    } catch (e: Exception) {
        println("Joined failed pseudoJob")
        println("Caught inside: $e")
    }

    try {
        println("Awaiting on pseudoJob")
        pseudoJob.await()
    } catch (e: Exception) {
        println("Awaited on failed pseudoJob")
        println("Caught inside: $e")
    }

    try {
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    pseudoJob to deferred
}

suspend fun f1AsyncNoJoinS(waitTime: Long) = supervisorScope {
    val pseudoJob: Deferred<Int> = async {
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    pseudoJob to deferred
}

suspend fun f1AsyncJoinAtEndS(waitTime: Long) = supervisorScope {
    val pseudoJob: Deferred<Int> = async {
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
        println("Awaiting on deferred")
        deferred.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    try {
        println("Joining pseudoJob")
        pseudoJob.join()
    } catch (e: Exception) {
        println("Exception thrown by pseudoJob.join(): $e")
        throw e
    }

    try {
        println("Awaiting on pseudoJob")
        pseudoJob.await()
    } catch (e: Exception) {
        println("Exception thrown by pseudoJob.await(): $e")
        throw e
    }

    pseudoJob to deferred
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

suspend fun f3() = coroutineScope {
    val deferred1 : Deferred<Int> = async {
        println("Throwing exception from async")
        throw ArithmeticException("From async")
    }

    val deferred2: Deferred<Int> = async {
        println("Computing deferred2 in async")
        2
    }

    try {
        deferred1.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    deferred1 to deferred2
}

suspend fun f4() = coroutineScope {
    val deferred1 : Deferred<Int> = async {
        println("Throwing exception from async")
        throw ArithmeticException("From async")
    }

    val deferred2: Deferred<Int> = async {
        println("Computing deferred2 in async")
        2
    }

    try {
        deferred1.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    deferred2.await()
}

suspend fun f5() = coroutineScope {
    val deferred1 : Deferred<Int> = async {
        println("Throwing exception from async")
        throw ArithmeticException("From async")
    }

    try {
        deferred1.await()
    } catch (e: Exception) {
        println("Awaited on deferred")
        println("Caught inside: $e")
    }

    2
}

suspend fun execute(str: String, block: suspend () -> Deferred<Any>) {
    println("***** $str")
    try {
        val result = block()
        println("result=$result")
        result.await()
    } catch (e: Exception) {
        println("Caught outside: $e")
    }
    println()
}


fun main() {
    runBlocking {
        println("***** async coroutineScope")
        try {
            val result: Deferred<Int> = coroutineScope {
                async { throw IllegalStateException("from async coroutineScope") }
            }
            println("result=$result")
            result.await()
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** async supervisorScope")
        try {
            val result: Deferred<Int> = supervisorScope {
                async { throw IllegalStateException("from async supervisorScope") }
            }
            println("result=$result")
            result.await()
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f0()")
        try {
            println(f0())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        execute("f1Launch(0)") { f1Launch(0).second }
        execute("f1LaunchNoJoin(0)") { f1LaunchNoJoin(0).second }
        execute("f1LaunchJoinAtEnd(0)") { f1LaunchJoinAtEnd(0).second }
        execute("f1Async(0)") { f1Async(0).second }
        execute("f1AsyncNoJoin(0)") { f1AsyncNoJoin(0).second }
        execute("f1AsyncJoinAtEnd(0)") { f1AsyncJoinAtEnd(0).second }

        execute("f1Launch(1000)") { f1Launch(1000).second }
        execute("f1LaunchNoJoin(1000)") { f1LaunchNoJoin(1000).second }
        execute("f1LaunchJoinAtEnd(1000)") { f1LaunchJoinAtEnd(1000).second }
        execute("f1Async(1000)") { f1Async(1000).second }
        execute("f1AsyncNoJoin(1000)") { f1AsyncNoJoin(1000).second }
        execute("f1AsyncJoinAtEnd(1000)") { f1AsyncJoinAtEnd(1000).second }

        execute("f1LaunchS(0)") { f1LaunchS(0).second }
        execute("f1LaunchNoJoinS(0)") { f1LaunchNoJoinS(0).second }
        execute("f1LaunchJoinAtEndS(0)") { f1LaunchJoinAtEndS(0).second }
        execute("f1AsyncS(0)") { f1AsyncS(0).second }
        execute("f1AsyncNoJoinS(0)") { f1AsyncNoJoinS(0).second }
        execute("f1AsyncJoinAtEndS(0)") { f1AsyncJoinAtEndS(0).second }

        execute("f1LaunchS(1000)") { f1LaunchS(1000).second }
        execute("f1LaunchNoJoinS(1000)") { f1LaunchNoJoinS(1000).second }
        execute("f1LaunchJoinAtEndS(1000)") { f1LaunchJoinAtEndS(1000).second }
        execute("f1AsyncS(1000)") { f1AsyncS(1000).second }
        execute("f1AsyncNoJoinS(1000)") { f1AsyncNoJoinS(1000).second }
        execute("f1AsyncJoinAtEndS(1000)") { f1AsyncJoinAtEndS(1000).second }

        println("***** f2()")
        try {
            println(f2())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f3()")
        try {
            println(f3())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f4()")
        try {
            println(f4())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f5()")
        try {
            println(f5())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()
    }
}
