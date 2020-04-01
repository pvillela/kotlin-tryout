package tryout.common

import kotlin.random.Random

fun wasteCpu(timeMillis: Long): Int {
    val startTime = System.currentTimeMillis()
    var result = 0
    while (true) { // computation loop, just wastes CPU
        print(".")
        for (i in 1..100)
            result = Random.nextInt()
        if (System.currentTimeMillis() >= startTime + timeMillis)
            break
    }
    return result
}
