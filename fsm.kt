
package ru.spbau.jirkov.fsm

import java.util.ArrayList
import java.util.HashMap

class FSM<E>(val startStateName: String) {

    private val states: MutableMap<String, State> = HashMap<String, State>();

    fun run() = { }
    fun addState(name: String) = states.put(name, State(name))

    var isDebugging: Boolean = false

    private fun debug(message: String) {
        if (isDebugging) {
            println(message)
        }
    }

    open inner class State(val name: String)
    {
        fun toString(): String = name

        val transitions: MutableList<Transition> = ArrayList<Transition>()
        var isFinal = false

        inner data class Transition(
                val symbol: E,
                val to: State
        )

        inner class TransitionBuilder(val sym: E) {
            fun minus (to: State) = { transitions.add(Transition(sym, to)); to }
        }

        fun minus(arrow: E): TransitionBuilder = TransitionBuilder(arrow)
    }

    fun get(name: String): State = states.get(name) ?: { val newstate = State(name); states.put(name, newstate); newstate }()

    fun run(sequence: Iterable<E>): Boolean {
        var current = this[startStateName]
        for ( s in sequence) {
            val next = current.transitions.find { it.symbol == s }
            when (next) {
                null -> {
                    debug("no transition to follow for symbol $s ! aborted in state: $current");
                    return false
                }
                else -> {
                    debug("from $current to ${next.to} through ${next.symbol}")
                    current = next.to
                }
            }
        }
        return current.isFinal
    }
}


fun main(args: Array<String>) {
//usage example

    val m = FSM<Char>("_")
    m["_"] - 't' - m["_t"]
    m["_t"] - 'h' - m["_th"]
    m["_th"] - 'e' - m["_the"]
    m["_th"] - 'i' - m["_thi"]
    m["_thi"] - 's' - m["_this"]
    m["_the"] - 's' - m["_thes"]
    m["_thes"] - 'e' - m["_these"]

    m["_this"].isFinal = true
    m["_these"].isFinal = true

    println(m.run("this".toCharList()))
    println(m.run("these".toCharList()))
    m.isDebugging = true

    println(m.run("thix".toCharList()))


}
