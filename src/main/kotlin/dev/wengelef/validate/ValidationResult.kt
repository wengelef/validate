package dev.wengelef.validate

sealed class ValidationResult<E, T> {
    class Valid<E, T>(val value: T) : ValidationResult<E, T>()
    class Invalid<E, T>(val errors: List<E>) : ValidationResult<E, T>()

    fun <R> fold(ifError: (List<E>) -> R, ifValid: (T) -> R) {
        when (this) {
            is Valid -> ifValid(value)
            is Invalid -> ifError(errors)
        }
    }

    fun <E2, T2> flatMap(map: (T) -> ValidationResult<E2, T2>): ValidationResult<E2, T2> {
        return when (this) {
            is Valid -> map(value)
            is Invalid -> this as ValidationResult<E2, T2>
        }
    }

    fun and(f: (T) -> ValidationResult<E, T>): ValidationResult<E, T> {
        return when (this) {
            is Invalid -> this
            is Valid -> f(value)
        }
    }

    fun <T2> map(ifInvalid: (E) -> T2, ifValid: (T) -> T2): T2 = when (this) {
        is Valid -> ifValid(value)
        is Invalid -> ifInvalid(errors.first())
    }

    fun isValid() = this is Valid<E, T>
}