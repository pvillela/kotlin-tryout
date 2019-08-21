/*
 * From https://medium.com/@elizarov/coroutine-context-and-scope-c8b255d59055
 */

package tryout

import kotlinx.coroutines.*
import kotlin.coroutines.*


fun main() = runBlocking<Unit> {
    launch { scopeCheck(this) }
    launch {
        println(this)
        println(this.coroutineContext)
        println(coroutineContext)
    }
}

suspend fun scopeCheck(scope: CoroutineScope) {
    println(scope.coroutineContext === coroutineContext)
}
