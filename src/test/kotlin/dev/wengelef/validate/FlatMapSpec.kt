package dev.wengelef.validate

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FlatMapSpec : FreeSpec() {

    init {
        val bob = User(18, "Bob")
        val userWithoutName = User(18, "")
        val unborn = User(-1, "")

        val isNotBornStringError = StringTypeError("Is not born")

        val isBorn = validate<StringTypeError, User> { User::age.validate(isNotBornStringError) { it >= 0 } }
        val isBlankName = validate<IntTypeError, User> { User::name.validate(IntTypeError(0)) { it.isNotBlank() } }

        "flatMap" should {
            "map Validations of different Types" {
                bob.isBorn()
                    .flatMap { bob.isBlankName() }
                    .isValid() shouldBe true
            }

            "map an invalid result" {
                unborn.isBorn()
                    .flatMap { userWithoutName.isBlankName() }
                    .fold({ errors ->
                        errors shouldContain isNotBornStringError
                    }, { })
            }
        }
    }

    internal class StringTypeError(val value: String)
    internal class IntTypeError(val value: Int)

    internal data class User(val age: Int, val name: String)
}