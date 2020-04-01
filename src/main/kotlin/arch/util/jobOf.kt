package arch.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


/**
 * Returns a new [Job] that is a child of [coroutineCtx]'s [Job], is used to define a new
 * [CoroutineScope] for execution of [block], and completes when all the child jobs created in the
 * new coroutine scope complete. Therefore, if this job is joined or cancelled then the child jobs
 * will be joined or cancelled, respectively.
 */
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


/**
 * Returns a new [Job] that is a child of the receiver's [Job], is used to define a new
 * [CoroutineScope] for execution of [block], and completes when all the child jobs created in the
 * new coroutine scope complete. Therefore, if this job is joined or cancelled then the child jobs
 * will be joined or cancelled, respectively.
 */
inline fun CoroutineScope.jobOf(block: CoroutineScope.() -> Any?): Job =
        jobOf(coroutineContext, block)
