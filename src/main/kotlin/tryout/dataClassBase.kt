package tryout

import java.time.Instant


data class Unique(
    val discriminator: String,
    val key1: String,
    val value1: String,
    val key2: String?,
    val value2: String?
)


interface Record {
    val unique: Unique
    val uuid: String?
    val timestamp: Instant
    val sourceTopicPartitionOffset: String?
}


data class BaseRecord(
    override val unique: Unique,
    override val uuid: String?,
    override val timestamp: Instant,
    override val sourceTopicPartitionOffset: String?
) : Record


data class SampleRecord1(
        val base: BaseRecord,
        val foo: Int,
        val bar: String
)


data class SampleRecord2(
        val base: BaseRecord,
        val foo: Int,
        val bar: String
) : Record by base
