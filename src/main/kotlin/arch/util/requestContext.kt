package arch.util

import org.slf4j.MDC
import reactor.util.context.Context


/**
 * Context information about the user that originated the call to the service. Used for logging,
 * data-based entitlement checking, and fine-grained role-based access control (authorization).
 */
data class UserContext(
        val userId: String,
        val roles: List<String>
)


/**
 * Context information that needs to be propagated and available on all threads involved in the
 * execution of a service, as well as upon resumption of coroutine suspensions. This class
 * encapsulates a UserContext instance, tracing headers, and the SLF4J MDC. [extra] is an optional
 * additional value to be added to the MDC, with the [EXTRA_KEY] key.
 */
data class RequestContext(
        val userCtx: UserContext,
        val tracingHeaders: Map<String, String>,
        val extra: String? = null
) {

    private val mdc0 = tracingHeaders + (USER_ID_KEY to userCtx.userId) +
            (ROLES_KEY to userCtx.roles.toString())

    /** [UserContext] informtion in `RequestContext` formatted as headers. */
    val userHeaders: Map<String, List<String>> =
            mapOf(USER_ID_KEY to listOf(userCtx.userId), ROLES_KEY to userCtx.roles)

    /** Informatinn from `RequestContext` formatted as a map compatible with `org.slf4j.MDC`. */
    val mdc: Map<String, String> = if (extra != null) mdc0 + (EXTRA_KEY to extra) else mdc0

    /** `traceId` value from [tracingHeaders]. */
    val traceId: String?
        get() = tracingHeaders[B3_TRACE_ID_KEY]

    /** `parentSpanId` value from [tracingHeaders]. */
    val parentSpanId: String?
        get() = tracingHeaders[B3_PARENT_SPAN_ID_KEY]

    /** `spanId` value from [tracingHeaders]. */
    val spanId: String?
        get() = tracingHeaders[B3_SPAN_ID_KEY]

    /** Returns a [reactor.util.context.Context] with the key-value pair ("request-context", this) */
    fun toSubscriberContext(): Context =
            Context.of(REQUEST_CONTEXT_KEY, this)

    /** Holds a thread-local `RequestContext` instance. */
    companion object {

        /** "request-context" */
        const val REQUEST_CONTEXT_KEY = "request-context"

        /** "user-id" */
        const val USER_ID_KEY = "user-id"

        /** "role" */
        const val ROLES_KEY = "role"

        // Request Header information needed for tracing through ISTIO and Jaeger

        /** "x-request-id" */
        const val REQUEST_ID_KEY = "x-request-id"

        /** "x-b3-traceid" */
        const val B3_TRACE_ID_KEY = "x-b3-traceid"

        /** "x-b3-parentspanid" */
        const val B3_PARENT_SPAN_ID_KEY = "x-b3-parentspanid"

        /** "x-b3-spanid" */
        const val B3_SPAN_ID_KEY = "x-b3-spanid"

        /** "x-b3-sampled" */
        const val B3_SAMPLED_KEY = "x-b3-sampled"

        /** "x-b3-flags" */
        const val B3_FLAGS_KEY = "x-b3-flags"

        /** "x-ot-span-context" */
        const val OT_SPAN_CONTEXT_KEY = "x-ot-span-context"

        /** Key ("extra") used for extra string-valued information to be added to MDC. */
        const val EXTRA_KEY = "extra"

        private val tlContext = ThreadLocal<RequestContext>()

        /** Returns thread-local `RequestContext` instance. */
        fun get(): RequestContext? = tlContext.get()

        /** Sets the thread-local `RequestContext` instance or clears it if [ctx] is `null`. */
        fun set(ctx: RequestContext?) {
            if (ctx != null) {
                tlContext.set(ctx)
                MDC.setContextMap(ctx.mdc)
            } else {
                clear()
            }
        }

        /** Clears the thread-local `RequestContext` instance. */
        private fun clear() {
            tlContext.remove()
            MDC.clear()
        }

        /**
         * Sets the thread-local `RequestContext` instance (see [set]) and returns the previously
         * existing one.
         * */
        fun swap(ctx: RequestContext?): RequestContext? {
            val oldCtx = get()
            set(ctx)
            return oldCtx
        }

        /** `userContext` attribute of thread-local `RequestContext` instancce. */
        val userContext: UserContext?
            get() = get()?.userCtx

        /** `mdc` attribute of thread-local `RequestContext` instancce. */
        val mdc: Map<String, String>?
            get() = get()?.mdc

        /** Constructs a `RequestContext` instancce from a [reactor.util.context.Context]. */
        fun of(sbsCtx: Context): RequestContext? =
            sbsCtx.getOrDefault(REQUEST_CONTEXT_KEY, null)
    }
}
