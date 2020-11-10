package app.cash.exhaustive.compiler

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.COMPILATION_ERROR
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.junit.Test

class ExhaustiveCompilerTest {
  @Test fun booleanExhaustiveCompiles() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

      fun subject(value: Boolean) {
        @Exhaustive
        when (value) {
          true -> println("yep")
          false -> println("nope")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(OK)
  }

  @Test fun booleanNonExhaustiveFailsToCompile() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

      fun subject(value: Boolean) {
        @Exhaustive
        when (value) {
          true -> println("yep")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(COMPILATION_ERROR)
    assertThat(result.messages).contains("@Exhaustive when is not exhaustive")
  }

  @Test fun booleanElseFailsToCompile() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

      fun subject(value: Boolean) {
        @Exhaustive
        when (value) {
          true -> println("yep")
          else -> println("nope")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(COMPILATION_ERROR)
    assertThat(result.messages).contains("@Exhaustive when must not contain an 'else' branch")
  }

  @Test fun enumExhaustiveCompiles() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

      enum class RouletteColor { Red, Black, Green }

      fun subject(value: RouletteColor) {
        @Exhaustive
        when (value) {
          RouletteColor.Red -> println("red")
          RouletteColor.Black -> println("black")
          RouletteColor.Green -> println("green")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(OK)
  }

  @Test fun enumNonExhaustiveFailsToCompile() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

      enum class RouletteColor { Red, Black, Green }

      fun subject(value: RouletteColor) {
        @Exhaustive
        when (value) {
          RouletteColor.Red -> println("red")
          RouletteColor.Black -> println("black")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(COMPILATION_ERROR)
    assertThat(result.messages).contains("@Exhaustive when is not exhaustive")
  }

  @Test fun enumElseFailsToCompile() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

      enum class RouletteColor { Red, Black, Green }

      fun subject(value: RouletteColor) {
        @Exhaustive
        when (value) {
          RouletteColor.Red -> println("red")
          RouletteColor.Black -> println("black")
          else -> println("green")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(COMPILATION_ERROR)
    assertThat(result.messages).contains("@Exhaustive when must not contain an 'else' branch")
  }

  @Test fun sealedClassExhaustiveCompiles() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

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
          RouletteColor.Green -> println("green")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(OK)
  }

  @Test fun sealedClassNonExhaustiveFailsToCompile() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

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
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(COMPILATION_ERROR)
    assertThat(result.messages).contains("@Exhaustive when is not exhaustive")
  }

  @Test fun sealedClassElseFailsToCompile() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

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
          else -> println("green")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(COMPILATION_ERROR)
    assertThat(result.messages).contains("@Exhaustive when must not contain an 'else' branch")
  }

  @Test fun noSubjectFailsToCompile() {
    val result = compile(
      """
      import app.cash.exhaustive.Exhaustive

      fun subject() {
        @Exhaustive
        when {
          true -> println("sup")
        }
      }
      """.trimIndent()
    )

    assertThat(result.exitCode).isEqualTo(COMPILATION_ERROR)
    assertThat(result.messages).contains("@Exhaustive when must have a subject expression")
  }

  private fun compile(@Language("kotlin") source: String): Result {
    return KotlinCompilation().apply {
      sources = listOf(SourceFile.kotlin("main.kt", source))
      messageOutputStream = System.out
      compilerPlugins = listOf(ExhaustiveComponentRegistrar())
      inheritClassPath = true
    }.compile()
  }
}
