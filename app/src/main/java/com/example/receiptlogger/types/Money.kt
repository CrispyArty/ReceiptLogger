package com.example.receiptlogger.types


data class Money(val cents: Long) {
//    constructor(cents: Int) : this(cents = cents.toLong())
//    constructor(notes: Double) : this(cents = (notes * 100).toLong())
//    constructor(notes: Float) : this(cents = (notes * 100).toLong())

    fun toNotes(): Double = (cents.toDouble() / 100)

    operator fun plus(prev: Money) : Money {
        return Money(this.cents + prev.cents)
    }
}

fun Long.toMoney() = Money(this)
fun Int.toMoney() = Money(this.toLong())
fun Double.toMoney() = Money((this * 100).toLong())
fun Float.toMoney() = Money((this * 100).toLong())

