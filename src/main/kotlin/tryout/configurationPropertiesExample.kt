package tryout

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans


@Configuration
//@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "pv")
open class DemoConfigurationProperties {
    var foo: String? = null
}

fun main(args: Array<String>) {

//    val context: ApplicationContext = AnnotationConfigApplicationContext(DemoConfigurationProperties::class.java)

    val context = GenericApplicationContext().apply {
        val myBeans = beans {
            bean<DemoConfigurationProperties>()
        }
        myBeans.initialize(this)
        refresh()
    }

    val cfgProps = context.getBean(DemoConfigurationProperties::class.java)

    println(cfgProps.foo)
}