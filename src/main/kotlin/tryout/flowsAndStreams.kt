package tryout

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield


object FlowsAndTimeouts {

    val ints: Flow<Int> = flow {
        for (i in 1..10) {
            val result = withTimeoutOrNull(50) {
                emit(i)
            }
            if (result == null)
                emit(1 * 100)
        }
    }

    @ExperimentalCoroutinesApi
    val intsC: Flow<Int> = channelFlow {
        for (i in 1..10) {
            val result = withTimeoutOrNull(50) {
                println("Sending $i")
                send(i)
            }
            if (result == null) {
                println("Sending ${i * 100}")
                send(1 * 100)
            }
        }
    }

    @ExperimentalCoroutinesApi
    val intsCC: Flow<Int> = channelFlow {
        val intakeChannel = Channel<Int>()
        val n = 10

        // Takes messages from intakeChannel and emits them
        launch {
            (1..n).forEach {
                yield()
                val i = intakeChannel.receive()
                println("Sending $i from intakeChannel.")
                send(i)
            }
            intakeChannel.close()
        }

        for (i in 1..n) {
            val result = withTimeoutOrNull(50) {
                println("Sending $i to intakeChannel")
                intakeChannel.send(i)
            }
            if (result == null) {
                println("Sending $i to intakeChannel")
                intakeChannel.send(i * 100)
            }
        }
    }.buffer(0)

    @ExperimentalCoroutinesApi
    suspend fun main() {
        println("\n***** ints *****")
        try {
            ints.collect {
                println("Collecting $it ...")
                delay(100)
                println("... collected $it")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        println("\n***** intsC *****")
        intsC.collect {
            println("Collecting $it ...")
            delay(100)
            println("... collected $it")
        }

        println("\n***** intsCC *****")
        intsCC.collect {
            println("Collecting $it ...")
            delay(100)
            println("... collected $it")
        }
    }
}


@ExperimentalCoroutinesApi
fun main() = runBlocking {
    FlowsAndTimeouts.main()
}
