package tryout

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

// See https://www.baeldung.com/jackson-inheritance


object JacksonPolymorphism1 {
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            // The line below is not necessary
//        include = JsonTypeInfo.As.PROPERTY,
            property = "discriminator"
    )
    @JsonSubTypes(  // avoid hardcoding discriminators here by using companion property
            JsonSubTypes.Type(value = Car::class, name = Car.discriminator),
            JsonSubTypes.Type(value = Truck::class, name = Truck.discriminator)
    )
    interface Vehicle {
        var foo: String
            get() = throw UnsupportedOperationException()
            set(x) = throw UnsupportedOperationException()
        val make: String
        val model: String
        val discriminator: String
        val bar: String
            get() = "bar"

        @JsonIgnore
        fun getBaz(): String

        // Jackson gets stack overflow when serializing the list of vehicles in main without the
        // annotation below
        @JsonIgnore
        fun <T : Vehicle> getXxx(): T
    }

    @JsonIgnoreProperties("foo", ignoreUnknown = true)
    data class Car(
            override val make: String,
            override val model: String,
            val seatingCapacity: Int,
            val topSpeed: Double
    ) : Vehicle {
        override val discriminator: String
            get() = Companion.discriminator

        override fun getBaz(): String = "Car.baz"

        override fun <T : Vehicle> getXxx(): T = Car("VW", "Beetle", 4, 90.0) as T

        companion object {
            const val discriminator: String = "Car"
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Truck(
            override val make: String,
            override val model: String,
            val payloadCapacity: Double,
            override var foo: String = "<<<TruckString>>>"
    ) : Vehicle {
        override val discriminator: String
            get() = Companion.discriminator

        override fun getBaz(): String = "Truck.baz"

        override fun <T : Vehicle> getXxx(): T = Truck("Mercedes", "M1", 100.0) as T

        companion object {
            const val discriminator: String = "Truck"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val objectMapper = ObjectMapper()
                .registerModule(KotlinModule())
                .registerModule(JavaTimeModule())

        val fleet: List<Vehicle> = listOf(
                Car("Toyota", "Rav4", 5, 150.0),
                Truck("Scania", "X25", 50_000.0)
        )

        val fleetSer = objectMapper.writeValueAsString(fleet)
        println(fleetSer)
        val fleetDes = objectMapper.readValue<List<Vehicle>>(fleetSer)
        println(fleetDes)
        println(fleet == fleetDes)
    }
}
