package tryout

import arch.util.jobOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import tryout.common.wasteCpu
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


object CoroutineMyGlobalScope {

    val log = LoggerFactory.getLogger(CoroutineMyGlobalScope::class.java)

    // Implementation identical to that of kotlinx.coroutines.GlobalScope
    val MyGlobalScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = EmptyCoroutineContext
    }

    // Examples below adapted from https://kotlinlang.org/docs/reference/coroutines/basics.html
    // and https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html

    fun main1() = runBlocking<Unit> { // start main coroutine
        MyGlobalScope.launch { // launch a new coroutine in background and continue
            delay(1000L)
            log.info("World!")
        }
        log.info("Hello,") // main coroutine continues here immediately
        delay(2000L)      // delaying for 2 seconds to keep JVM alive
    }

    fun main2() = runBlocking {
        val job = MyGlobalScope.launch { // launch a new coroutine and keep a reference to its Job
            delay(1000L)
            log.info("World!")
        }
        log.info("Hello,")
        job.join() // wait until child coroutine completes
    }

    fun main3() = runBlocking<Unit> {
        // launch a coroutine to process some kind of incoming request
        val request = launch {
            // it spawns two other jobs, one with GlobalScope
            MyGlobalScope.launch {
                log.info("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                log.info("job1: I am not affected by cancellation of the request")
            }
            // and the other inherits the parent context
            launch {
                delay(100)
                log.info("job2: I am a child of the request coroutine")
                delay(1000)
                log.info("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // cancel processing of the request
        delay(1000) // delay a second to see what happens
        log.info("main: Who has survived request cancellation?")
    }

    fun main4() = runBlocking<Unit> { // start main coroutine
        MyGlobalScope.launch { // launch a new coroutine in background and continue
//            delay(1000L)
            wasteCpu(3000L)
            log.info("World!")
        }
        log.info("Hello,") // main coroutine continues here immediately
        // no delay to keep JVM alive
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("\n***** main1")
        main1()

        println("\n***** main2")
        main2()

        println("\n***** main3")
        main3()

        println("\n***** main4")
        main4()
    }
}
