package arch.util

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.withTimeoutOrNull


/**
 * Suspends until it receives an item from [this] channel or [timeout] is exceeded, returns the
 * received item if [timeout] is not exceeded, `null` otherwise.
 */
suspend fun <T : Any> ReceiveChannel<T>.receiveWithTimeout(timeout: Long): T? =
        withTimeoutOrNull(timeout) {
            receive()
        }
