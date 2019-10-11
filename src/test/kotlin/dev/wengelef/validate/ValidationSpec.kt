package dev.wengelef.validate

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class ValidationSpec : FreeSpec() {

    init {
        "Valid Data" should {
            "have no Errors and be valid" {
                val validate = validate<ValidationError, ComplexData> {
                    ComplexData::stringValue.validate(stringValError) { it.isNotBlank() }
                    ComplexData::intValue.validate(intValError) { it == 20 }
                    ComplexData::floatValue.validate(floatValError) { it == 5.5f }
                    ComplexData::complexValue.validate {
                        ComplexNested::stringValue.validate(nestedStringError) { it.toInt() == 18 }
                        ComplexNested::complexValue.validate {
                            ComplexNested1::intValue.validate(nested1IntError) { it < 0 }
                        }
                    }
                }

                complexData.validate()
                    .fold(
                        { },
                        { value -> value shouldBe complexData }
                    )

                complexData.validate().isValid() shouldBe true
            }
        }

        "Invalid Data" should {
            "should have all errors and not be valid" {
                val validate = validate<ValidationError, ComplexData> {
                    ComplexData::stringValue.validate(stringValError) { it.isEmpty() }
                    ComplexData::intValue.validate(intValError) { it != 20 }
                    ComplexData::floatValue.validate(floatValError) { it != 5.5f }
                    ComplexData::complexValue.validate {
                        ComplexNested::stringValue.validate(nestedStringError) { it.toInt() == 19 }
                        ComplexNested::complexValue.validate {
                            ComplexNested1::intValue.validate(nested1IntError) { it >= 0 }
                        }
                    }
                }

                complexData.validate()
                    .fold(
                        { errors ->
                            errors shouldContainExactly listOf(
                                stringValError,
                                intValError,
                                floatValError,
                                nestedStringError,
                                nested1IntError
                            )
                        },
                        { }
                    )

                complexData.validate().isValid() shouldBe false
            }

            "Bill" should {
                val isOldEnough = validate<String, User> { User::age.validate("Is not old enough") { it > 18 } }
                "be old enough to Drink" {
                    bill.isOldEnough().shouldBeTypeOf<ValidationResult.Valid<String, User>>()
                }
            }

            "Bob" should {
                val isOldEnough = validate<String, User> { User::age.validate("Is not old enough") { it > 18 } }
                "not be old enough to Drink" {
                    bob.isOldEnough().shouldBeTypeOf<ValidationResult.Invalid<String, User>>()
                    bob.isOldEnough().fold(
                        { errorMsg -> errorMsg shouldContain "Is not old enough" },
                        { }
                    )
                }
            }
        }
    }

    internal companion object {
        val complexData = ComplexData(
            "StringValue",
            20,
            5.5f,
            ComplexNested(
                "18",
                ComplexNested1(-1)
            )
        )

        val bob = User(17)
        val bill = User(21)

        val stringValError = ValidationError("StringValue is Blank")
        val intValError = ValidationError("IntValue is not 20")
        val floatValError = ValidationError("FloatValue is not 5.5")
        val nestedStringError = ValidationError("Nested String is not numeric")
        val nested1IntError = ValidationError("Nested1 Int is Zero or greater")
    }

    internal data class User(val age: Int)

    internal data class ValidationError(val msg: String)

    internal data class ComplexData(
        val stringValue: String,
        val intValue: Int,
        val floatValue: Float,
        val complexValue: ComplexNested
    )

    internal data class ComplexNested(
        val stringValue: String,
        val complexValue: ComplexNested1
    )

    internal data class ComplexNested1(
        val intValue: Int
    )
}