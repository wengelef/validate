package dev.wengelef.validate

import kotlin.reflect.KProperty1

@DslMarker
annotation class ValidationDslMarker

@ValidationDslMarker
class ValidationDSL<E, T>(private val instance: T) {

    private val validations = mutableListOf<Validation<E, T>>()

    fun <P> KProperty1<T, P>.validate(e: E, predicate: (P) -> Boolean) {
        val validation = Validation.cond(predicate(get(instance)), { e }, { instance })
        validations.add(validation)
    }

    fun <P> KProperty1<T, P>.validate(dsl: ValidationDSL<E, P>.() -> Unit) {
        val subValidation = ValidationDSL<E, P>(get(instance))
            .apply(dsl)
            .validate()
            .map { value -> value.map { instance } }

        validations.addAll(subValidation)
    }

    //fun isValid() = validations.none { it is Validation.Left }

    private fun validate(): List<Validation<E, T>> {
        return validations
    }

    fun toResult(): ValidationResult<E, T> {
        return validations
            .let { values ->
                val errors = values.filterIsInstance<Validation.Left<E, T>>()

                if (errors.isEmpty()) {
                    ValidationResult.Valid(values.first { it is Validation.Right }.right())
                } else {
                    ValidationResult.Invalid(errors.map { value -> value.left() })
                }
            }
    }
}