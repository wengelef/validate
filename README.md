[![Build Status](https://travis-ci.org/wengelef/validate.svg?branch=master)](https://travis-ci.org/wengelef/validate)

# validate
typesafe Validation DSL

## Usage
```kotlin
class User(val name: String, val age: Int)

val bob = User("Bob", 21)
val bill = User("Bill", 20)

val isOldEnoughToDrink = validate<String, User> {
    User::age.validate("is not old enough to drink") { it >= 21 }
}

bob.isOldEnoughToDrink()
        .fold({ errors -> errors.forEach { println(it) } }, { user -> println("${user.name} is old enough to drink!") })
// prints 'Bob is old enough to drink!'

bill.isOldEnoughToDrink()
        .fold({ errors -> errors.forEach { println(it) } }, { user -> println("${user.name} is old enough to drink!") })
// prints 'is not old enough to drink'
```

## Complex Error Types
```kotlin
sealed class ValidationError {
    object NameIsBlank : ValidationError()
    object NotOldEnough : ValidationError()
}

val validate = validate<ValidationError, User> {
    User::age.validate(ValidationError.NotOldEnough) { it >= 18 }
    User::name.validate(ValidationError.NameIsBlank) { it.isNotBlank() }
}

bob.validate()
        .fold(::handleValidationErrors, ::printIsOldEnoughToDrink)
// prints 'Bob is old enough to drink!'

blank.validate()
        .fold(::handleValidationErrors, ::printIsOldEnoughToDrink)
// prints 'ValidationError$NotOldEnough'
// prints 'ValidationError$NameIsBlank'

blankName.validate()
        .fold(::handleValidationErrors, ::printIsOldEnoughToDrink)
// prints 'ValidationError$NameIsBlank'
```

## Download
gradle
```groovy
implementation 'dev.wengelef:validate:0.1.2'
```

maven
```xml
<dependency>
  <groupId>dev.wengelef</groupId>
  <artifactId>validate</artifactId>
  <version>0.1.2</version>
  <type>pom</type>
</dependency>
```
