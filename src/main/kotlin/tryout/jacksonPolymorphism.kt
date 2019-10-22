package tryout

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

// See https://www.baeldung.com/jackson-inheritance


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        // The line below is not necessary
//        include = JsonTypeInfo.As.PROPERTY,
        property = "discriminator"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = Car::class, name = "Car"),
        JsonSubTypes.Type(value = Truck::class, name = "Truck")
)
interface Vehicle {
    val make: String
    val model: String
    val discriminator: String
}

data class Car(
        override val make: String,
        override val model: String,
        val seatingCapacity: Int,
        val topSpeed: Double
) : Vehicle {
    override val discriminator: String
        get() = "Car"
}

data class Truck(
        override val make: String,
        override val model: String,
        val payloadCapacity: Double
) : Vehicle {
    override val discriminator: String
        get() = "Truck"
}


/////////////////////
// main

fun main() {
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
