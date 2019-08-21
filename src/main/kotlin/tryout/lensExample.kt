package tryout


// File 1

data class ComplexObject(
        var foo: Int,
        var bar: String,
        var baz: Long,
        var xyz: Int,
        var dkdk: String
)

// File 2

fun ComplexObject.make(cls: Class<ActivityAbcInput>) =
        ActivityAbcInput(foo, bar)

fun ComplexObject.push(obj: ActivityAbcOutput) {
    xyz = obj.xyz
    dkdk = obj.dkdk
}

// File 3

data class ActivityAbcInput(
    val foo: Int,
    val bar: String
)

data class ActivityAbcOutput(
        val xyz: Int,
        val dkdk: String
)

class ActivityAbc {
    operator fun invoke(input: ActivityAbcInput): ActivityAbcOutput =
            ActivityAbcOutput(input.foo + 1, input.bar + "1")
}

// main

fun main() {
    val co = ComplexObject(1, "2", 3, 4, "5")
    val input = co.make(ActivityAbcInput::class.java)
    val output = ActivityAbc()(input)

    println(input)
    println(output)
}
