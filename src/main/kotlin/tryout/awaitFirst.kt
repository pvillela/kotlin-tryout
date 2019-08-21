package tryout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Mono


fun main() {
    val mono = Mono.fromSupplier {
        println(Thread.currentThread())
        1
    }

    runBlocking {
        val x = mono.awaitFirst()
        println(x)

        val x1 = async(Dispatchers.Default) {
            delay(100)
            mono.awaitFirst()
        }

        val x2 = async(Dispatchers.Default) {
            mono.awaitFirst()
        }

        println(x1.await() + x2.await())
    }
}
