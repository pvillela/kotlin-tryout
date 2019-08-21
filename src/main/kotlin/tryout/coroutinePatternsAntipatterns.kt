package tryout

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/*
 * Based on https://proandroiddev.com/kotlin-coroutines-patterns-anti-patterns-f9d12984c68e
 */

fun makeExample(
        f: CoroutineScope.(Logger) -> Unit
): CoroutineScope.(String) -> Unit = { name ->
    val log = LoggerFactory.getLogger(name)
    log.info("Entering ${log.name}");
    this.f(log)
    log.info("About to exit ${log.name}");
}


///////////////////
// Wrap async calls with coroutineScope or use SupervisorJob to handle exceptions


val example1A = makeExample { log ->

    fun CoroutineScope.doWork(): Deferred<String> {
        return async {
            log.info("About to throw exception")
            throw Exception("BOOM ${log.name}")
        }
    }

    // doWork() inside launch creates a child coroutine of the one created by launch.
    this.launch {
        try {
            val deferred = doWork()
            log.info("deferred=$deferred")
            deferred.await()
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}

val example1B = makeExample { log ->
    val scope = this

    fun CoroutineScope.doWork(): Deferred<String> {
        return async {
            log.info("About to throw exception")
            throw Exception("BOOM ${log.name}")
        }
    }

    // doWork() inside launch creates a sibling coroutine of the one created by launch.
    this.launch {
        try {
            val deferred = scope.doWork()
            log.info("deferred=$deferred")
            deferred.await()
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1C = makeExample() { log ->
    // async inside launch creates a child coroutine of the one created by launch.
    this.launch {
        try {
            val deferred = async {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("deferred=$deferred")
            deferred.await()
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1D = makeExample() { log ->
    val scope = this
    // async inside launch creates a sibling coroutine of the one created by launch.
    this.launch {
        try {
            val deferred = scope.async {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("deferred=$deferred")
            deferred.await()
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1E = makeExample() { log ->
    // async inside launch creates a child coroutine of the one created by launch.
    this.launch {
        try {
            val deferred = async {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("deferred=$deferred")
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1F = makeExample() { log ->
    val scope = this
    // async inside launch creates a sibling coroutine of the one created by launch.
    this.launch {
        try {
            val deferred = scope.async {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("deferred=$deferred")
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1G = makeExample() { log ->
    // async inside launch creates a child coroutine of the one created by launch.
    this.launch {
        try {
            val job = launch {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("job=$job")
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1H = makeExample() { log ->
    val scope = this
    // async inside launch creates a sibling coroutine of the one created by launch.
    this.launch {
        try {
            val job = scope.launch {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("job=$job")
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1I = makeExample() { log ->
    // async inside async creates a child coroutine of the one created by launch.
    val deferred0 = this.async {
        try {
            val deferred = async {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("job=$deferred")
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


val example1J = makeExample() { log ->
    val scope = this
    // async inside async creates a sibling coroutine of the one created by launch.
    val deferred0 = this.launch {
        try {
            val deferred = scope.async {
                log.info("About to throw exception")
                throw Exception("BOOM ${log.name}")
            }
            log.info("job=$deferred")
        } catch (e: Exception) {
            log.info("+++++ Caught in loadData: $e")
        }
        log.info(("Exiting launch"))
    }
}


// If async block may throw exception don’t rely on wrapping it with try/catch block.

// One way how you can avoid the crash is by using SupervisorJob


///////////////////
// Run examples

suspend fun runExample(name: String, f: CoroutineScope.(String) -> Unit) {
    val log = LoggerFactory.getLogger("runExample: $name")

    println("\n*** $name ***")
    try {
        coroutineScope {
            log.info("About to execute f")
            f(name)
            log.info("Completed runExample")
        }
    } catch (e: Exception) {
        log.info("***** Caught in runExample: $e")
    }
}

suspend fun runExampleS(name: String, f: CoroutineScope.(String) -> Unit) {
    val log = LoggerFactory.getLogger("runExample: $name")

    println("\n*** $name ***")
    try {
        supervisorScope {
            log.info("About to execute f")
            f(name)
            log.info("Completed runExampleS")
        }
    } catch (e: Exception) {
        log.info("***** Caught in runExampleS: $e")
    }
}

fun main() = runBlocking {

    runExample("example1A", example1A)
    runExample("example1B", example1B)
    runExample("example1C", example1C)
    runExample("example1D", example1D)
    runExample("example1E", example1E)
    runExample("example1F", example1F)
    runExample("example1G", example1G)
    runExample("example1H", example1H)
    runExample("example1I", example1I)
    runExample("example1J", example1J)

    runExampleS("example1AS", example1A)
    runExampleS("example1BS", example1B)
    runExampleS("example1CS", example1C)
    runExampleS("example1DS", example1D)
    runExampleS("example1ES", example1E)
    runExampleS("example1FS", example1F)
    runExampleS("example1GS", example1G)
    runExampleS("example1HS", example1H)
    runExampleS("example1IS", example1I)
    runExampleS("example1JS", example1J)

    delay(500)
    println("\n##### Completed runBlocking")
}
