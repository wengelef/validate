package dev.wengelef.validate

import java.lang.IllegalStateException

sealed class Validation<L, R> {
    class Left<L, R>(val l: L) : Validation<L, R>()
    class Right<L, R>(val r: R) : Validation<L, R>()

    fun right() = if (this is Right) r else throw IllegalStateException()
    fun left() = if (this is Left) l else throw IllegalStateException()

    fun <T> fold(ifLeft: (L) -> T, ifRight: (R) -> T): T {
        return when (this) {
            is Left -> ifLeft(l)
            is Right -> ifRight(r)
        }
    }

    fun <L2, R2> bimap(ifLeft: (L) -> L2, ifRight: (R) -> R2): Validation<L2, R2> {
        return when (this) {
            is Left -> Left(ifLeft(l))
            is Right -> Right(ifRight(r))
        }
    }

    fun <R2> map(f: (R) -> R2): Validation<L, R2> {
        return when (this) {
            is Left -> Left(l)
            is Right -> Right(f(r))
        }
    }

    companion object {
        fun <L, R> cond(predicate: Boolean, ifLeft: () -> L, ifRight: () -> R): Validation<L, R> {
            return if (predicate) Right(ifRight()) else Left(ifLeft())
        }

        fun <L, R> right(t: R): Validation<L, R> = Right(t)
        fun <L, R> left(e: L): Validation<L, R> = Left(e)
    }
}