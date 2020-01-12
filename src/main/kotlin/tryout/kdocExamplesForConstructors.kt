package tryout


object KdocExamplesForConstructors {
    /**
     * This is Foo. Foo does foo.
     */
    class Foo {

        /** This one does something else. */
        val param2: Int
        val x: String

        // param1 documentation is not picked-up by Dokka.
        /**
         * This is the primary constructor.
         * @param param1  This is param1.
         */
        constructor(
                param1: String,
                param2: Int
        ) {
            println(param1)
            this.param2 = param2
            this.x = param1 + "xxx"
        }
    }

    /**
     * This is Foo1. Foo does bar.
     */
    class Foo1

// param1 documentation is not picked-up by Dokka.
    /**
     * This is the primary constructor.
     * @param param1  This is param1.
     */
    constructor(
            param1: String,
            /** This one does something else. */
            val param2: Int
    ) {
        val x = param1 + "xxx"
    }

// param1 documentation is picked-up by Dokka but placed at the class level, not with the
// constructor.
    /**
     * This is Foo2. Foo does baz.
     *
     * @param param1  This is param1
     */
    class Foo2(
            param1: String,
            /** This one does something else. */
            val param2: Int
    ) {
        val x = param1 + "xxx"
    }
}
