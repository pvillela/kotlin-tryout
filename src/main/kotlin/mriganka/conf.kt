package presto.demo.fareacceptanceservice.boot

import propsview.arch.PropsView
import presto.arch.config.readExternalProperties
import presto.arch.config.readLocalProperties
import presto.arch.hystrix.ConfigProperties

import org.apache.kafka.clients.producer.ProducerConfig
import java.util.Properties


internal val localProps = readLocalProperties()

object LocalConf : PropsView(localProps) {

    val configServerUrl: String by key("fare-acceptance-service.config-server-url")
    val label: String by key("fare-acceptance-service.label")
    val profile: String by key("fare-acceptance-service.profile")
    val uri: String = configServerUrl + "/" + profile
}


internal val externalProps = readExternalProperties(LocalConf.uri)

object Conf {
    object serviceUrls : PropsView(externalProps, "fare-acceptance-service") {
        val mediaServiceUrl: String by key("fmedia-service-url")
        val mediaServiceUri: String by key("media-service-uri")
    }

    object kafka : PropsView(externalProps, "fare-acceptance-service") {
        val topic: String by key("fare-acceptance-service.kafka-topic")
        val kafkaProperties: Properties = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props["fare-acceptance-service.bootstrap.servers"])
            put("sasl.jaas.config", props["fare-acceptance-service.sasl.jaas.config"])
        }
    }

    object hystrix : PropsView(externalProps, "fare-acceptance-service.hystrix.command.default") {

        val executionTimeoutInMilliseconds: Int by key("execution.isolation.thread.timeoutInMilliseconds")
        val circuitBreakerRequestVolumeThreshold: Int by key("circuitBreaker.requestVolumeThreshold")
        val circuitBreakerSleepWindowInMilliseconds: Int by key("circuitBreaker.sleepWindowInMilliseconds")
        val circuitBreakerErrorThresholdPercentage: Int by key("circuitBreaker.errorThresholdPercentage")

        val configProperties = ConfigProperties().apply {
            executionTimeoutInMilliseconds = hystrix.executionTimeoutInMilliseconds
            circuitBreakerRequestVolumeThreshold = hystrix.circuitBreakerRequestVolumeThreshold
            circuitBreakerSleepWindowInMilliseconds = hystrix.circuitBreakerSleepWindowInMilliseconds
            circuitBreakerErrorThresholdPercentage = hystrix.circuitBreakerErrorThresholdPercentage
        }
    }
}
