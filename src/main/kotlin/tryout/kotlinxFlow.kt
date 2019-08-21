/*
 * Based on https://medium.com/@elizarov/kotlin-flows-and-coroutines-256260fb3bdb
 */

@file:UseExperimental(FlowPreview::class, ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)


package tryout

import kotlin.system.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*


object `Sequential flows 1` {

    val ints: Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }

    suspend fun main() {
        println(this::class.simpleName)
        val time = measureTimeMillis {
            ints.collect { println(it) }
        }
        println("Collected in $time ms")
    }
}


object `Sequential flows 2` {

    val ints: Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }

    suspend fun main() {
        println()
        println(this::class.simpleName)
        val time = measureTimeMillis {
            ints.collect {
                delay(100)
                println(it)
            }
        }
        println("Collected in $time ms")
    }
}


object `Concurrent coroutines` {

    val ints: Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }

    fun <T> Flow<T>.buffer(size: Int = 0): Flow<T> = flow {
        coroutineScope {
            val channel = produce(capacity = size) {
                collect { send(it) }
            }
            channel.consumeEach { emit(it) }
        }
    }

    suspend fun main() {
        val time = measureTimeMillis {
            ints.buffer().collect {
                delay(100)
                println(it)
            }
        }
        println("Collected in $time ms")
    }
}


fun main() = runBlocking {
    `Sequential flows 1`.main()
    `Sequential flows 2`.main()
    `Concurrent coroutines`.main()
}
