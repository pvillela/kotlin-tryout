/*
 * Based on https://medium.com/@elizarov/reactive-streams-and-kotlin-flows-bfd12772cda4
 */

@file:UseExperimental(FlowPreview::class, ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)


package tryout

import kotlin.system.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*


object FlowsAndStreams {

    val ints: Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
        }
    }

    fun <T> Flow<T>.onCompleted(action: () -> Unit): Flow<T> = flow {
        // reemit all values from the original flow
        collect { value -> emit(value) }
        // this code runs only after the normal completion
        action()
    }

    suspend fun main() {
        println(this::class.simpleName)
        val intsA = ints.onCompleted { println("Completed ints") }.map { it * 10 }
        intsA.collect { println(it) }
    }
}


fun main() = runBlocking {
    FlowsAndStreams.main()
}
