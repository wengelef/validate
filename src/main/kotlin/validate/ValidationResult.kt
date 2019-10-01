package util.validate

sealed class ValidationResult<E, T> {
    class Valid<E, T>(val value: T) : ValidationResult<E, T>()
    class Invalid<E, T>(val errors: List<E>) : ValidationResult<E, T>()

    fun <R> fold(ifError: (List<E>) -> R, ifValid: (T) -> R) {
        when (this) {
            is Valid -> ifValid(value)
            is Invalid -> ifError(errors)
        }
    }
}