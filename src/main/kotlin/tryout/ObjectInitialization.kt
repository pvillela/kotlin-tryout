package tryout

// object initialization is lazy, upon first use of the object reference.
object ObjectInitialization {

    object Foo {
        init {
            println("Initializaing object Foo.")
        }
    }

    object Bar {
        init {
            println("Initializaing object Bar.")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("Assigning object Foo to a val.")
        val foo = Foo

        println("Accessing object Foo.")
        println(Foo)

        println("Accessing object Bar.")
        println(Bar)
    }
}