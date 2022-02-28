package tryout

fun main() {
    val digitalSignature = 1 shl 7
    val nonRepudiation = 1 shl 6
    val keyEncipherment = 1 shl 5
    val dataEncipherment = 1 shl 4
    val keyAgreement = 1 shl 3
    val keyCertSign = 1 shl 2
    val cRLSign = 1 shl 1
    val encipherOnly = 1 shl 0
    val decipherOnly = 1 shl 15

    println(digitalSignature)
    println(nonRepudiation)
    println(keyEncipherment)
    println(dataEncipherment)
    println(keyAgreement)
    println(keyCertSign)
    println(cRLSign)
    println(encipherOnly)
    println(decipherOnly)
}
