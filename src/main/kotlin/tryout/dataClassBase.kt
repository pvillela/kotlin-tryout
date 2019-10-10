package tryout

import java.time.Instant
import java.util.*


data class Unique(
    val discriminator: String,
    val key1: String?,
    val value1: String?,
    val key2: String?,
    val value2: Int?
) {
    companion object
}

val Unique.sortNbr: Int?
    get() = if (this.key2 == "sortNbr") this.value2 else null

val Unique.uuid: String?
    get() = if (this.key1 == "UUID") this.value1 else null

fun Unique.Companion.makeWithSortNbr(discriminator: String, sortNbr: Int) =
        Unique(discriminator, null, null, "sortNbr", sortNbr)

fun Unique.Companion.makeWithUuid(discriminator: String) =
        Unique(discriminator, "uuid", UUID.randomUUID().toString(), null, null)

private val foo: Unique = Unique.makeWithSortNbr("foo", 99)

private val bar: Unique = Unique.makeWithUuid("bar")


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
) : Record {
    companion object
}

fun BaseRecord.Companion.makeWithSortNbr(
        discriminator: String,
        sortNbr: Int,
        uuid: String?,
        sourceTopicPartitionOffset: String?
): BaseRecord {
    val unique = Unique.makeWithSortNbr(discriminator, sortNbr)
    val timestamp = Instant.now()
    return BaseRecord(unique, uuid, timestamp, sourceTopicPartitionOffset)
}

fun BaseRecord.Companion.makeWithUuid(
        discriminator: String,
        sourceTopicPartitionOffset: String?
): BaseRecord {
    val unique = Unique.makeWithUuid(discriminator)
    val timestamp = Instant.now()
    return BaseRecord(unique, unique.uuid, timestamp, sourceTopicPartitionOffset)
}


data class SampleRecord1(
        val base: BaseRecord,
        val foo: Int,
        val bar: String
)


data class SampleRecord2(
        val base: BaseRecord,
        val foo: Int,
        val bar: String
) : Record by base {
    companion object
}

fun SampleRecord2.Companion.makeWithSortNbr(
        sortNbr: Int,
        uuid: String?,
        sourceTopicPartitionOffset: String?,
        foo: Int,
        bar: String
): SampleRecord2 {
    val base = BaseRecord.makeWithSortNbr("SampleRecord2", sortNbr, uuid, sourceTopicPartitionOffset)
    return SampleRecord2(base, foo, bar)
}

fun SampleRecord2.Companion.makeWithUuid(
        sourceTopicPartitionOffset: String?,
        foo: Int,
        bar: String
): SampleRecord2 {
    val base = BaseRecord.makeWithUuid("SampleRecord2", sourceTopicPartitionOffset)
    return SampleRecord2(base, foo, bar)
}
