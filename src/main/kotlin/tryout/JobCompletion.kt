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
            coroutineCtx: CoroutineContext,
            block: CoroutineScope.() -> Any?
    ): Job {
        val newJob = Job(coroutineCtx[Job])
        val newScope = CoroutineScope(coroutineCtx + newJob)
        with(newScope, block)
        newJob.complete()  // now, newJob will complete when its children complete.
        return newJob
    }

    inline fun jobOfNoComplete(
            coroutineCtx: CoroutineContext,
            block: CoroutineScope.() -> Any?
    ): Job {
        val newJob = Job(coroutineCtx[Job])
        val newScope = CoroutineScope(coroutineCtx + newJob)
        with(newScope, block)
        return newJob
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        launch {
            val job1 = jobOf(coroutineContext) {
                println("Running job1")
            }
            job1.join()
            println("Joined job1")
        }

        val job2a = launch {
            val job2 = jobOfNoComplete(coroutineContext) {
                println("Running job2")
            }
            job2.join()
            println("Joined job2")
        }

        launch {
            val job3 = jobOf(coroutineContext) {
                println("Running job3")
            }
            job3.join()
            println("Joined job3")
        }

        delay(1000)
        println("Finished waiting 1000ms.")

        println("""If "Joined job2 wasn't printed then job2 didn't complete. """)
        job2a.cancel()
        println("job2a cancelled.")
    }
}
