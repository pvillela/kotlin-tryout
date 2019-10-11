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


interface MediaHeaderI {
    val unique: Unique
    val uuid: String?
    val timestamp: Instant
    val sourceTopicPartitionOffset: String?
    val mediaKey: String
}


interface CredentialCardHeaderI : MediaHeaderI {
    val mediaId: String
    val mediaProviderId: Int
    val mediaType: Int
    override val mediaKey: String
        get() = mediaId + mediaProviderId + mediaType
}


data class CredentialCardHeader(
        override val unique: Unique,
        override val uuid: String?,
        override val timestamp: Instant,
        override val sourceTopicPartitionOffset: String?,
        override val mediaId: String,
        override val mediaProviderId: Int,
        override val mediaType: Int
) : CredentialCardHeaderI {

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


/////////////////////
// Embedding example

interface MediaEmb {
    val header: MediaHeaderI
}

interface CredentialCardEmb : MediaEmb {
    override val header: CredentialCardHeaderI
}

data class UsageEmb(
        override val header: CredentialCardHeader,
        val foo: String,
        val bar: Int
) : CredentialCardEmb {
    companion object
}

fun UsageEmb.Companion.makeWithSortNbr(
        sortNbr: Int,
        uuid: String?,
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int,
        foo: String,
        bar: Int
): UsageEmb {
    val header = CredentialCardHeader.makeWithSortNbr("Usage", sortNbr, uuid, sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
    return UsageEmb(header, foo, bar)
}

fun UsageEmb.Companion.makeWithUuid(
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int,
        foo: String,
        bar: Int
): UsageEmb {
    val header = CredentialCardHeader.makeWithUuid("Usage", sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
    return UsageEmb(header, foo, bar)
}


/////////////////////
// Inheritance example

typealias MediaInh = MediaHeaderI

typealias CredentialCardInh = CredentialCardHeaderI

data class UsageInh(
        override val unique: Unique,
        override val uuid: String?,
        override val timestamp: Instant,
        override val sourceTopicPartitionOffset: String?,
        override val mediaId: String,
        override val mediaProviderId: Int,
        override val mediaType: Int,
        val foo: String,
        val bar: Int
) : CredentialCardInh {

    constructor(header: CredentialCardHeader, foo:String, bar: Int) : this(
            header.unique,
            header.uuid,
            header.timestamp,
            header.sourceTopicPartitionOffset,
            header.mediaId,
            header.mediaProviderId,
            header.mediaType,
            foo,
            bar
    )

    companion object
}

fun UsageInh.Companion.makeWithSortNbr(
        sortNbr: Int,
        uuid: String?,
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int,
        foo: String,
        bar: Int
): UsageInh {
    val header = CredentialCardHeader.makeWithSortNbr("Usage", sortNbr, uuid, sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
    return UsageInh(header, foo, bar)
}

fun UsageInh.Companion.makeWithUuid(
        sourceTopicPartitionOffset: String?,
        mediaId: String,
        mediaProviderId: Int,
        mediaType: Int,
        foo: String,
        bar: Int
): UsageInh {
    val base = CredentialCardHeader.makeWithUuid("Usage", sourceTopicPartitionOffset, mediaId, mediaProviderId, mediaType)
    return UsageInh(base, foo, bar)
}
