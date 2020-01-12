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


object JacksonPolymorphism2 {
    // Below annotation has almost the same effect as the other two, except that the property
    // value ends up with a prepended ".".
//    @JsonTypeInfo (include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "_type_")
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "_type_"
    )
    @JsonSubTypes(
            JsonSubTypes.Type(value = Car::class),
            JsonSubTypes.Type(value = Truck::class)
    )
    interface Vehicle {
        var foo: String
            get() = throw UnsupportedOperationException()
            set(x) = throw UnsupportedOperationException()
        val make: String
        val model: String
        val bar: String
            get() = "bar"

        @JsonIgnore
        fun getBaz(): String
    }

    @JsonIgnoreProperties("foo", ignoreUnknown = true)
    data class Car(
            override val make: String,
            override val model: String,
            val seatingCapacity: Int,
            val topSpeed: Double
    ) : Vehicle {
        override fun getBaz(): String = "Car.baz"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Truck(
            override val make: String,
            override val model: String,
            val payloadCapacity: Double,
            override var foo: String = "<<<TruckString>>>"
    ) : Vehicle {
        override fun getBaz(): String = "Truck.baz"
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

//         Below doesn't work: see https://groups.google.com/forum/#!topic/jackson-user/cRDRW7q5BEw
//        val fleetDes = objectMapper.readValue<List<Vehicle>>(fleetSer)
//        println(fleetDes)
//        println(fleet == fleetDes)

        val car: Vehicle = Car("Toyota", "Rav4", 5, 150.0)
        val carSer = objectMapper.writeValueAsString(car)
        println(carSer)
        val carDes = objectMapper.readValue<Vehicle>(carSer)
        println(carDes)
        println(car == carDes)
    }
}
