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


interface MediaRecordHeader {
    val unique: Unique
    val uuid: String?
    val timestamp: Instant
    val sourceTopicPartitionOffset: String?
    val mediaKey: String
}


interface MediaRecord {
    val header: MediaRecordHeader
}


data class CredentialCardHeader(
        override val unique: Unique,
        override val uuid: String?,
        override val timestamp: Instant,
        override val sourceTopicPartitionOffset: String?,
        val mediaId: String,
        val mediaProviderId: Int,
        val mediaType: Int
) : MediaRecordHeader {
    override val mediaKey: String
        get() = mediaId + mediaProviderId + mediaType

    companion object
}

fun CredentialCardHeader.Companion.makeWithSortNbr(
        discriminator: String,
        sortNbr: Int,
        uuid: String?,
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int
): CredentialCardHeader {
    val unique = Unique.makeWithSortNbr(discriminator, sortNbr)
    val timestamp = Instant.now()
    return CredentialCardHeader(unique, uuid, timestamp, sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
}

fun CredentialCardHeader.Companion.makeWithUuid(
        discriminator: String,
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int
): CredentialCardHeader {
    val unique = Unique.makeWithUuid(discriminator)
    val timestamp = Instant.now()
    return CredentialCardHeader(unique, unique.uuid, timestamp, sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
}

data class Usage(
        override val header: CredentialCardHeader,
        val foo: String,
        val bar: Int
) : MediaRecord {
    companion object
}

fun Usage.Companion.makeWithSortNbr(
        sortNbr: Int,
        uuid: String?,
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int,
        foo: String,
        bar: Int
): Usage {
    val header = CredentialCardHeader.makeWithSortNbr("Usage", sortNbr, uuid, sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
    return Usage(header, foo, bar)
}

fun Usage.Companion.makeWithUuid(
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int,
        foo: String,
        bar: Int
): Usage {
    val base = CredentialCardHeader.makeWithUuid("Usage", sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
    return Usage(base, foo, bar)
}
