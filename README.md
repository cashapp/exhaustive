Exhaustive
==========

An annotation and Kotlin compiler plugin for enforcing a `when` statement is exhaustive.

```kotlin
enum class RouletteColor { Red, Black, Green }

fun printColor(color: RouletteColor) {
  @Exhaustive
  when (subject) {
    Red -> println("red")
    Black -> println("black")
  }
}
```
```
e: Example.kt:5: @Exhaustive when is not exhaustive!
```

No more assigning to dummy local properties or referencing pointless functions or properties to
force the `when` to be an expression for exhaustiveness checking. The plugin reuses the same check
that is used for a `when` expression.

In addition to being forced to be exhaustive, an annotated `when` statement is forbidden from using
an `else` branch.

```kotlin
fun printColor(color: RouletteColor) {
  @Exhaustive
  when (subject) {
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

fun subject(value: RouletteColor) {
  @Exhaustive
  when (value) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }
}
```
```
e: Example.kt:9: @Exhaustive when is not exhaustive!
```

Vote for [youtrack.jetbrains.com/issue/KT-12380](https://youtrack.jetbrains.com/issue/KT-12380)
to see this added to the Kotlin language (with a better syntax).


Usage
-----

```groovy
buildscript {
  dependencies {
    classpath 'app.cash.exhaustive:exhaustive-gradle:0.1.0'
  }
  repositories {
    mavenCentral()
  }
}

apply plugin: 'org.jetbrains.kotlin.jvm' // or .android or .multiplatform
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
    classpath 'app.cash.exhaustive:exhaustive-gradle:0.1.0-SNAPSHOT'
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


License
=======

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
