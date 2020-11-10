package app.cash.exhaustive.gradle

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ExhaustivePluginTest(
  private val fixtureName: String,
) {
  companion object {
    @JvmStatic
    @Parameters(name = "{0}")
    fun parameters() = listOf(
      arrayOf("android"),
      arrayOf("android-unit-test"),
      arrayOf("android-instrumentation-test"),
      arrayOf("js"),
      arrayOf("js-test"),
      arrayOf("jvm"),
      arrayOf("jvm-test"),
      arrayOf("mpp"),
      arrayOf("mpp-test"),
    )
  }

  private val fixturesDir = File("src/test/fixture")

  private fun versionProperty() = "-PexhaustiveVersion=$exhaustiveVersion"

  @Test fun build() {
    // KotlinCompilerPluginSupportPlugin does not work with instrumentation compilations yet.
    assumeTrue(fixtureName != "android-instrumentation-test")

    val fixtureDir = File(fixturesDir, fixtureName)
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
      .withProjectDir(fixtureDir)
      .withArguments("clean", "build", "--stacktrace", versionProperty())
      .buildAndFail()
    assertThat(result.output).contains("BUILD FAILED")
    assertThat(result.output).contains(
      """
      |Example.kt: (9, 3): @Exhaustive when is not exhaustive!
      |
      |Missing branches:
      |- RouletteColor.Green
      """.trimMargin()
    )
  }
}
