package tryout


fun main() {
    val str = "abc"
    val strList = listOf(str)
    val intList = strList as List<Int>
    try {
        intList.map { it * 2 }
    } catch (e: Exception) {
        println(e)
    }

    val intFun = { i: Int -> i + 1 }
    val strFun = intFun as (String) -> String
    try {
        strFun("abc")
    } catch (e: Exception) {
        println(e)
    }
}
