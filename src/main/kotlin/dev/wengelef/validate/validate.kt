package dev.wengelef.validate

private fun <E, T> validate(t: T, f: ValidationDSL<E, T>.() -> Unit): ValidationResult<E, T> {
    return ValidationDSL<E, T>(t).apply(f).validate()
        .let { values ->
            val errors = values.filterIsInstance<Validation.Left<E, T>>()

            if (errors.isEmpty()) {
                ValidationResult.Valid(values.first { it is Validation.Right }.right())
            } else {
                ValidationResult.Invalid(errors.map { value -> value.left() })
            }
        }
}

fun <E, T> validate(f: ValidationDSL<E, T>.() -> Unit): T.() -> ValidationResult<E, T> = { validate(this, f) }