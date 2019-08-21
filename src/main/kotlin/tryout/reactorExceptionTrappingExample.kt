package tryout

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.AssertionError
import java.util.ServiceConfigurationError


fun throwInMono(e: Throwable) {
    try {
        val res = Mono.fromSupplier { throw e }.materialize().block()
        println("Throwable ${e::class.java.toString().drop(6)} stayed within mono, with signal exception = ${res?.throwable}.")
    } catch (e: Throwable){
        println("Throwable ${e::class.java.toString().drop(6)} ESCAPED mono.")
    }
}

fun main(args: Array<String>) {
    println(Flux.fromIterable(listOf("a", "b")).single().materialize().block())

    throwInMono(Exception(">>> non-fatal"))
    throwInMono(InterruptedException(">>> FATAL"))
    throwInMono(Error(">>> non-fatal but certain subtypes are fatal"))
    throwInMono(ServiceConfigurationError(">>> non-fatal"))
    throwInMono(AssertionError(">>> non-fatal"))
    throwInMono(StackOverflowError(">>> non-fatal subtype of VirtualMachineError"))
    throwInMono(OutOfMemoryError(">>> FATAL subtype of VirtualMachineError"))
    throwInMono(InternalError(">>> FATAL subtype of VirtualMachineError"))
}
