package tryout

import java.math.BigInteger

// See http://herongyang.com/Cryptography/RSA-BigInteger-Convert-Byte-Sequences-to-Positive-Integers.html
fun  main() {
    val bytesA = ByteArray(1)
    bytesA[0] = 0x7f  // 127
    val bytesB = ByteArray(1)
    bytesB[0] = 0x80.toByte()  // -128
    val bytesC = ByteArray(2)
    bytesC[0] = 0
    bytesC[1] = 0xff.toByte()  // -1
    val bigIntegerA = BigInteger(bytesA)
    val bigIntegerA1 = BigInteger(1, bytesA)
    val bigIntegerB = BigInteger(bytesB)
    val bigIntegerB1 = BigInteger(1, bytesB)
    val bigIntegerC = BigInteger(bytesC)
    val bigIntegerC1 = BigInteger(1, bytesC)

    println(bigIntegerA)
    println(bigIntegerA.toByteArray().toList())
    println(bigIntegerA1)
    println(bigIntegerA1.toByteArray().toList())
    println(bigIntegerB)
    println(bigIntegerB.toByteArray().toList())
    println(bigIntegerB1)
    println(bigIntegerB1.toByteArray().toList())
    println(bigIntegerC)
    println(bigIntegerC.toByteArray().toList())
    println(bigIntegerC1)
    println(bigIntegerC1.toByteArray().toList())
    println(BigInteger.valueOf(128))
    println(BigInteger.valueOf(128).toByteArray().toList())
    println(BigInteger.valueOf(255))
    println(BigInteger.valueOf(255).toByteArray().toList())
    println(BigInteger.valueOf(256))
    println(BigInteger.valueOf(256).toByteArray().toList())
}

