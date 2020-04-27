package arch.util

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext


/**
 * Supports propagation of [RequestContext] across coroutine suspensions/resumptions.
 */
class RequestContextElement(val reqCtx: RequestContext?) : ThreadContextElement<RequestContext?> {

    /** Companion object for a key of this element in coroutine context. */
    companion object Key : CoroutineContext.Key<RequestContextElement>

    /** Provides the key of the corresponding context element. */
    override val key: CoroutineContext.Key<RequestContextElement>
        get() = Key

    /** Invoked before coroutine is resumed on current thread. */
    override fun updateThreadContext(context: CoroutineContext): RequestContext? {
        val oldReqCtx = RequestContext.swap(reqCtx)
        return oldReqCtx
    }

    /** Invoked after coroutine has suspended on current thread. */
    override fun restoreThreadContext(context: CoroutineContext, oldState: RequestContext?) {
        RequestContext.set(oldState)
    }
}