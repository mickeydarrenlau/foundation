package gay.pizza.foundation.common

fun <T> Array<T>.without(value: T): List<T> = filter { it != value }
