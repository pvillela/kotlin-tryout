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


fun main() {
    runBlocking {
        println("***** f0()")
        try {
            println(f0())
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1Launch(0)")
        try {
            println(f1Launch(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchNoJoin(0)")
        try {
            println(f1LaunchNoJoin(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchJoinAtEnd(0)")
        try {
            println(f1LaunchJoinAtEnd(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1Async(0)")
        try {
            println(f1Async(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncNoJoin(0)")
        try {
            println(f1AsyncNoJoin(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncJoinAtEnd(0)")
        try {
            println(f1AsyncJoinAtEnd(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1Launch(1000)")
        try {
            println(f1Launch(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchNoJoin(1000)")
        try {
            println(f1LaunchNoJoin(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchJoinAtEnd(1000)")
        try {
            println(f1LaunchJoinAtEnd(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1Async(1000)")
        try {
            println(f1Async(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncNoJoin(1000)")
        try {
            println(f1AsyncNoJoin(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncJoinAtEnd(1000)")
        try {
            println(f1AsyncJoinAtEnd(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchS(0)")
        try {
            println(f1LaunchS(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchNoJoinS(0)")
        try {
            println(f1LaunchNoJoinS(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchJoinAtEndS(0)")
        try {
            println(f1LaunchJoinAtEndS(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncS(0)")
        try {
            println(f1AsyncS(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncNoJoinS(0)")
        try {
            println(f1AsyncNoJoinS(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncJoinAtEndS(0)")
        try {
            println(f1AsyncJoinAtEndS(0))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchS(1000)")
        try {
            println(f1LaunchS(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchNoJoinS(1000)")
        try {
            println(f1LaunchNoJoinS(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1LaunchJoinAtEndS(1000)")
        try {
            println(f1LaunchJoinAtEndS(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncS(1000)")
        try {
            println(f1AsyncS(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncNoJoinS(1000)")
        try {
            println(f1AsyncNoJoinS(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

        println("***** f1AsyncJoinAtEndS(1000)")
        try {
            println(f1AsyncJoinAtEndS(1000))
        } catch (e: Exception) {
            println("Caught outside: $e")
        }
        println()

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
