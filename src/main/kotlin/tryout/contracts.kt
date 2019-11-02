package tryout

import kotlin.contracts.*

// Based on https://www.baeldung.com/kotlin-contracts


// Contracts are only allowed for top-level functions
@ExperimentalContracts
private fun validate(request: Contracts.Request?) {
    contract {
        returns() implies (request != null)
    }
    if (request == null) {
        throw IllegalArgumentException("Undefined request")
    }
    if (request.arg.isBlank()) {
        throw IllegalArgumentException("No argument is provided")
    }
}

// Contracts are only allowed for top-level functions
@ExperimentalContracts
private fun isInterested(event: Any?): Boolean {
    contract {
        returns(true) implies (event is Contracts.MyEvent)
    }
    return event is Contracts.MyEvent
}

// Contracts are only allowed for top-level functions
@ExperimentalContracts
inline fun <R> myRun(block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}

// Contracts are only allowed for top-level functions
// This function abuses the contract
@ExperimentalContracts
inline fun <R> myAbusiveRun(block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
    return block()
}

private object Contracts {

    data class Request(val arg: String)

    @ExperimentalContracts
    fun process(request: Request?) {
        validate(request)
        println(request.arg) // Compiles fine now
    }

    data class MyEvent(val message: String)

    @ExperimentalContracts
    fun processEvent(event: Any?) {
        if (isInterested(event)) {
            println(event.message)
        }
    }

    fun runExample() {
        val i: Int
        var k: Int = 0
        run {
            k++
            i = k
        }
        println("runExample: $i")
    }

    @ExperimentalContracts
    fun myRunExample1() {
        val i: Int
        var k: Int = 0
        myRun {
            k++
            i = k
        }
        println("myRunExample1: $i")
    }

    @ExperimentalContracts
    fun myRunExample2() {
        val i: Int
        var k: Int = 0
        myRun {
            k++
            i = k
        }
        val x = myRun {
            val a = 1
            val b = 2
            a + b + i
        }
        println("myRunExample2: $x")
    }

    // Assigns to val after it has been initialized
    @ExperimentalContracts
    fun myAbusiveRunExample() {
        val i: Int
        var k: Int = 0
        myAbusiveRun {
            k++
            i = k
        }
        // j should be 1 but ends up being 2 because the above block is executed twice by
        // myAbusiveRun, in violation of its contract.
        println("myAbusiveRunExample: $i")
    }
}

@ExperimentalContracts  // This annotation is needed transitively
fun main() {
    val request1 = Contracts.Request("req1")
    val request2 = Contracts.Request("")
    Contracts.process(request1)
    try {
        Contracts.process(request2)
    } catch (e: Exception) {
        println(e)
    }

    val myEvent = Contracts.MyEvent("myEvent")
    Contracts.processEvent(myEvent)

    Contracts.runExample()
    Contracts.myRunExample1()
    Contracts.myRunExample2()
    Contracts.myAbusiveRunExample()
}
