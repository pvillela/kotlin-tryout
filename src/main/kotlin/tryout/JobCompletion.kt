package tryout

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object JobCompletion {

    inline fun jobOf(
            coroutineCtx: CoroutineContext = EmptyCoroutineContext,
            block: CoroutineScope.() -> Any?
    ): Job {
        val newJob = Job(coroutineCtx[Job])
        val newScope = CoroutineScope(coroutineCtx + newJob)
        with(newScope, block)
        newJob.complete()  // now, newJob will complete when its children complete.
        return newJob
    }

    inline fun jobOfNoComplete(
            coroutineCtx: CoroutineContext = EmptyCoroutineContext,
            block: CoroutineScope.() -> Any?
    ): Job {
        val newJob = Job(coroutineCtx[Job])
        val newScope = CoroutineScope(coroutineCtx + newJob)
        with(newScope, block)
        return newJob
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job1 = jobOf {
            launch { println("Running job1") }
        }

        val job2 = jobOfNoComplete {
            launch { println("Running job2") }
        }

        job1.join()
        println("Joined job1")

        val job3 = launch {
            job2.join()
            println("Joined job2")
        }
        delay(1000)
        println("""If "Joined job2" wasn't printed then job2 didn't complete. """)
        job3.cancel()
    }
}
