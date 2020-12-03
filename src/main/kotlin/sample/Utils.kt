package sample

import org.slf4j.Logger
import org.slf4j.LoggerFactory
// import kotlin.reflect.full.companionObject

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

// fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
//     return javaClass.enclosingClass?.takeIf {
//         it.kotlin.companionObject?.java == javaClass
//     } ?: javaClass
// }

private val log = LoggerFactory.getLogger("kt.sample.Utils")

public val THING = 10

public fun process(names: Collection<String>): Int {
    log.info("hi there")

    return names.map{
        val l = it.length
        println("$it -> $l")
        l
    }.sum()
}
