package dev.wengelef.validate

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class AndSpec : FreeSpec() {

    init {
        val bob = User(18, "Bob")
        val bill = User(21, "Bill")
        val unborn = User(-1, "")

        val isBorn = validate<String, User> { User::age.validate("Is not born") { it >= 0 } }
        val isBlankName = validate<String, User> { User::name.validate("Name is Blank") { it.isNotBlank() } }

        "and" should {
            "chain valid validations of different Subjects" {
                bob.isBorn()
                    .and { bill.isBorn() }
                    .isValid() shouldBe true
            }

            "chain valid validations on the same Subject" {
                bob.isBorn()
                    .and(isBlankName)
                    .isValid() shouldBe true
            }

            "contain errors if one of the chained validation fails" {
                val result1 = unborn.isBorn()
                    .and(isBlankName)

                result1.fold({ errors -> errors shouldContainExactly listOf("Is not born") }, { })

                val result2 = unborn.isBlankName()
                    .and(isBorn)

                result2.fold({ errors -> errors shouldContainExactly listOf("Name is Blank") }, { })
            }

            "be Invalid if one fails" {
                unborn.isBorn()
                    .and { bob.isBorn() }
                    .isValid() shouldBe false

                bob.isBorn()
                    .and { unborn.isBorn() }
                    .isValid() shouldBe false
            }

            "contain errors if one fails" {
                unborn.isBorn().and { bob.isBorn() }
                    .fold(
                        { errors -> errors shouldContain "Is not born" },
                        { }
                    )
            }
        }
    }

    internal data class User(val age: Int, val name: String)
}