package tryout

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory


fun main() {
    val log = LoggerFactory.getLogger("coroutineScopeExample")
    val scope = CoroutineScope(Dispatchers.Default + Job())

    runBlocking {

        println("\n********************")
        println("coroutineScope doesn't wait for coroutines running on non-nested scopes")
        coroutineScope {

            log.info("Started coroutineScope")

            val deferred1 = scope.async {
                log.info("In scope.async 1, before delay ...")
                delay(20000)
                log.info("... completed scope.async 1 after delay")
            }

            val deferred2 = scope.async {
                log.info("In scope.async 2, before delay ...")
                delay(20000)
                log.info("... scope.async 2 after delay, about to throw")
                throw Exception("Boom scope.async 2")
            }

            scope.launch {
                log.info("In scope.launch 1, before delay ...")
                delay(20000)
                log.info("... completed scope.launch 1 after delay")
            }

            scope.launch {
                log.info("In scope.launch 2, before delay ...")
                delay(20000)
                log.info("... scope.launch 2 after delay, about to throw")
                throw Exception("Boom scope.launch 2")
            }
        }

        println("\n********************")
        println("coroutineScope completes with success if all nested coroutines succeed")
        val res = coroutineScope {

            val deferred1a = async {
                log.info("In async 1, before delay ...")
                delay(1)
                log.info("... completed async 1 after delay")
            }

            scope.launch {
                log.info("In launch 1, before delay ...")
                delay(1)
                log.info("... completed launch 1 after delay")
            }

            42
        }

        log.info("res=$res")

        println("\n********************")
        println("""If nested coroutines complete with exceptions, coroutineScope completes with
            the first exception to occur""")
        try {
            coroutineScope {
                val deferred2a = async {
                    log.info("In async 2, before delay ...")
                    delay(200)
                    log.info("... async 2 after delay, about to throw")
                    throw Exception("Boom async 2")
                }

                launch {
                    log.info("In launch 2, before delay ...")
                    delay(100)
                    log.info("... launch 2 after delay, about to throw")
                    throw Exception("Boom launch 2")
                }
            }
        } catch (e: java.lang.Exception) {
            log.info("Caught $e in runBlocking")
        }

        println("\n********************")
        println("""Again, if nested coroutines complete with exceptions, coroutineScope
            completes withthe first exception to occur""")
        try {
            coroutineScope {
                val deferred2a = async {
                    log.info("In async 3, before delay ...")
                    delay(100)
                    log.info("... async 3 after delay, about to throw")
                    throw Exception("Boom async 3")
                }

                launch {
                    log.info("In launch 3, before delay ...")
                    delay(200)
                    log.info("... launch 3 after delay, about to throw")
                    throw Exception("Boom launch 2")
                }
            }
        } catch (e: java.lang.Exception) {
            log.info("Caught $e in runBlocking")
        }

        println()
        log.info("*** Exiting runBlocking")
    }

    log.info("*** Exited runBlocking")
}
