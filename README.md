# Exhaustive

An annotation and Kotlin compiler plugin for enforcing a `when` statement is exhaustive.

```kotlin
enum class RouletteColor { Red, Black, Green }

fun printColor(color: RouletteColor) {
  @Exhaustive
  when (color) {
    Red -> println("red")
    Black -> println("black")
  }
}
```
```
e: Example.kt:5: @Exhaustive when is not exhaustive!

Missing branches:
- RouletteColor.Green
```

No more assigning to dummy local properties or referencing pointless functions or properties to
force the `when` to be an expression for exhaustiveness checking. The plugin reuses the same check
that is used for a `when` expression.

In addition to being forced to be exhaustive, an annotated `when` statement is forbidden from using
an `else` branch.

```kotlin
fun printColor(color: RouletteColor) {
  @Exhaustive
  when (color) {
    Red -> println("red")
    Black -> println("black")
    else -> println("green")
  }
}
```
```
e: Example.kt:5: @Exhaustive when must not contain an 'else' branch
```

The presence of an `else` block indicates support for a default action. The exhaustive check would
otherwise always pass with this branch which is why it is disallowed.

Sealed classes are also supported.

```kotlin
sealed class RouletteColor {
  object Red : RouletteColor()
  object Black : RouletteColor()
  object Green : RouletteColor()
}

fun printColor(color: RouletteColor) {
  @Exhaustive
  when (color) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }
}
```
```
e: Example.kt:9: @Exhaustive when is not exhaustive!

Missing branches:
- RouletteColor.Green
```

Vote for [youtrack.jetbrains.com/issue/KT-12380](https://youtrack.jetbrains.com/issue/KT-12380)
to see this added to the Kotlin language (with a better syntax).


## Usage

```groovy
buildscript {
  dependencies {
    classpath 'app.cash.exhaustive:exhaustive-gradle:0.1.1'
  }
  repositories {
    mavenCentral()
  }
}

apply plugin: 'org.jetbrains.kotlin.jvm' // or .android or .multiplatform or .js
apply plugin: 'app.cash.exhaustive'
```

The `@Exhaustive` annotation will be made available in your main and test source sets but will not
be shipped as a dependency of the module.

<details>
<summary>Snapshots of the development version are available in Sonatype's snapshots repository.</summary>
<p>

```groovy
buildscript {
  dependencies {
    classpath 'app.cash.exhaustive:exhaustive-gradle:0.2.0-SNAPSHOT'
  }
  repositories {
    maven {
      url 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
  }
}

// 'apply' same as above
```

</p>
</details>


## Alternatives Considered

In the weeks prior to building this project a set of alternatives were explored and rejected
for various reasons. They are listed below. If you evaluate their merits differently, you are
welcome to use them instead. The solution provided by this plugin is not perfect either.

### Unused local and warning suppression

```kotlin
fun printColor(color: RouletteColor) {
  @Suppress("UNUSED_VARIABLE")
  val exhaustive = when (color) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }
}
```

Pros:
 - Works everywhere without library or plugin
 - No overhead or impact on compiled code

Neutral:
 - Somewhat self-describing as to the intent, assuming good local property names
 - Good locality as the exhaustiveness forcing is very close to the `when` keyword, although
   somewhat overshadowed by the warning suppression

Cons:
 - Requires suppression of warning which need to be put into a shared template or
   requires alt+enter,enter-ing to create the final form
 - Requires the use of _unique_ local property names (`_` is not allowed here)

### Built-in trailing property or function call

```kotlin
fun printColor(color: RouletteColor) {
  when (color) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }.javaClass // or .hashCode() or anything else...
}
```

Pros:
 - Works everywhere without library or plugin

Cons:
 - Not self-describing as to the effect on the `when` and the developer intent behind adding it
 - Poor locality as the property is far away from the `when` keyword it modifies
 - Impact on compiled code in the form of a property call, function call, and/or additional
   instructions at the call-site

### Library trailing property

```kotlin
@Suppress("unused") // Receiver reference forces when into expression form.
inline val Any?.exhaustive get() = Unit

fun printColor(color: RouletteColor) {
  when (color) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }.exhaustive
}
```

Pros:
 - Self-describing effect on the `when`
 - No impact on compiled code

Cons:
 - Requires a library
 - Poor locality as the property is far away from the `when` keyword it modifies
 - Pollutes the extension namespace by showing up for everything, not just `when`

### Library leading expression

```kotlin
object exhaustive {
  inline operator fun minus(other: Any?) = Unit
}

fun printColor(color: RouletteColor) {
  exhaustive-when (color) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }
}
```

Pro:
 - Great locality as the syntactical trick appears almost like a soft keyword modifying the `when`

Neutral:
 - Slight impact on compiled code (which could be mitigated on Android with an embedded R8 rule)

Cons:
 - Requires a library
 - Feels too clever
 - Code formatting will insert a space before and after the minus sign breaking the effect

### Use soft keyword in compiler

```kotlin
fun printColor(color: RouletteColor) {
  sealed when (color) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }
}
```

Pros:
 - Great locality as a soft keyword directly modifying the `when`
 - No impact on compiled code
 - Part of the actual language

Cons:
 - Requires forking the compiler and IDE plugin which is an overwhelming long-term commitment!!!


# License

    Copyright 2020 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
