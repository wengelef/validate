package dev.wengelef.validate

fun <E, T> validate(t: T, f: ValidationDSL<E, T>.() -> Unit): ValidationResult<E, T> {
    return ValidationDSL<E, T>(t)
        .apply(f)
        .toResult()
}

fun <E, T> validate(f: ValidationDSL<E, T>.() -> Unit): T.() -> ValidationResult<E, T> = { validate(this, f) }