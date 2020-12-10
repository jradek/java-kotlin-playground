package sample

import org.slf4j.LoggerFactory

fun getLogger(forClass: Class<*>) = LoggerFactory.getLogger(forClass)

private val log = LoggerFactory.getLogger("kt.sample.Utils")

const val THING = 10

fun process(names: Collection<String>): Int {
    log.info("hi there")

    return names.map{
        val l = it.length
        println("$it -> $l")
        l
    }.sum()
}
