package sample.app

import sample.Foo
import sample.THING
import sample.getLogger
import sample.process

class App {
    val greeting: String
        get() {
            return "Hello world. from Kotlin"
        }

    fun saySomething(s: String) {
        log.info("Hi $s")
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val log = getLogger(javaClass.enclosingClass)
    }
}

fun doit() {
    for (i in 1..4) {
        println(i)
    }
}

fun main(args: Array<String>) {
    println(App().greeting)
    println(App().saySomething("foo"))

    val f = Foo()
    println(f.message)
    val f1 = Foo("Michael")
    println(f1.message)

    doit()
    println(sample.THING)

    val c = process(f1.names)
    println("Count is $c")

    for (i in -1..1) {
        val v = f1.getSome(i).map {
            println("Got something $it")
            it
        }.orElse(run {
            println("nothing")
            666
        })
        println("$i -> $v")
    }
}
