package tryout

import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.*


fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("monoTee")

    val scheduler: Scheduler = Schedulers.parallel()

    fun g(x: Int): Mono<Int> {
        log.info("g called with input $x")
        return Mono.fromSupplier {
            log.info("g's mono with input $x starting to execute")
            Thread.sleep((Random().nextDouble() * 100).toLong())
            val res = x + 1
            log.info("g's mono with input $x completed with value $res")
            res
        }.publishOn(scheduler)
    }

    val mono = g(1)
    val monoCached = mono.cache()

    println(mono.block())
    println(mono.block())
    println(mono.block())

    println(monoCached.block())
    println(monoCached.block())
    println(monoCached.block())
}
