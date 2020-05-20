package tryout

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
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
    val intsCatch1: Flow<Int> = flow {
        for (i in 1..10) {
            try {
                withTimeout(50) {
                    emit(i)
                }
            } catch (e: TimeoutCancellationException) {
                // Can't let a CancellationException rip through
                throw Exception("boom")
            }
        }
    }.catch {
        emit(42)
    }

    @ExperimentalCoroutinesApi
    val intsCatch2: Flow<Int> = flow {
        for (i in 1..10) {
            withTimeoutOrNull(50) { // can't use withTimeout
                emit(i)
            }
        }
    }.catch {
        emit(42)
    }

    @ExperimentalCoroutinesApi
    val intsChannelFlow: Flow<Int> = channelFlow {
        for (i in 1..10) {
            val result = withTimeoutOrNull(50) {
                println("Sending $i")
                send(i)
            }
            if (result == null) {
                println("Sending ${i * 100}")
                send(i * 100)
            }
        }
    }.buffer(0)

    @ExperimentalCoroutinesApi
    val intsIntakeChannel: Flow<Int> = channelFlow {
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

        println("\n***** intsCatch1 *****")
        intsCatch1.collect {
            println("Collecting $it ...")
            delay(100)
            println("... collected $it")
        }

        println("\n***** intsCatch2 *****")
        intsCatch2.collect {
            println("Collecting $it ...")
            delay(100)
            println("... collected $it")
        }

        println("\n***** intsChannelFlow *****")
        intsChannelFlow.collect {
            println("Collecting $it ...")
            delay(100)
            println("... collected $it")
        }

        println("\n***** intsIntakeChannel *****")
        intsIntakeChannel.collect {
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
