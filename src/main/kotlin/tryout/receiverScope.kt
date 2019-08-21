package tryout

import tryout.ReceiverScope.A
import tryout.ReceiverScope.B
import tryout.ReceiverScope.C
import tryout.ReceiverScope.efa
import tryout.ReceiverScope.efab
import tryout.ReceiverScope.efac
import tryout.ReceiverScope.efb
import tryout.ReceiverScope.efbc
import tryout.ReceiverScope.efc


object ReceiverScope {

    class A {
        fun mfa() { println("mfa") }
        fun mfab() { println("mfab-a") }
        fun mfac() { println("mfac-a") }
    }

    class B {
        fun mfb() { println("mfb") }
        fun mfab() { println("mfab-b") }
        fun mfbc() { println("mfbc-b") }
    }

    class C {
        fun mfc() { println("mfc") }
        fun mfac() { println("mfac-c") }
        fun mfbc() { println("mfbc-c") }
    }

    fun A.efa() { println("efa") }
    fun A.efab() { println("efab-a") }
    fun A.efac() { println("efac-a") }

    fun B.efb() { println("efb") }
    fun B.efab() { println("efab-b") }
    fun B.efbc() { println("efbc-b") }

    fun C.efc() { println("efc") }
    fun C.efac() { println("efac-c") }
    fun C.efbc() { println("efbc-c") }
}


fun main() {
    val a = A()
    a.apply {
        val b = B()
        b.apply {
            val c = C()
            c.apply {
                mfa()
                mfab()
                mfac()
                mfb()
                mfbc()
                mfc()
                efa()
                efab()
                efac()
                efb()
                efbc()
                efc()
            }
        }
    }
}