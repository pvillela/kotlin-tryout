package tryout

import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.Random


fun main() {
    val log = LoggerFactory.getLogger("monoDelay")

    val scheduler: Scheduler = Schedulers.parallel()

    fun g(x: Int): Mono<Int> {
        log.info("g called with input $x")
        return Mono.delay(Duration.ofMillis(100)).map {
            val res = x + 1
            log.info("g's mono with input $x completed with value $res")
            res
        }.publishOn(scheduler)
    }

    val mono = g(1)

    println(mono.block())
    Thread.sleep(200)
    println(mono.block())
    println(mono.block())
}
