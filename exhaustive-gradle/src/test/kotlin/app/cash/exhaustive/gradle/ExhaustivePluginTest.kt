package app.cash.exhaustive.gradle

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class ExhaustivePluginTest {
  private val fixturesDir = File("src/test/fixture")

  private fun versionProperty() = "-PexhaustiveVersion=$exhaustiveVersion"

  @Test fun jvm() {
    val fixtureDir = File(fixturesDir, "jvm")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
      .withProjectDir(fixtureDir)
      .withArguments(
        "clean", "compileKotlin", "--stacktrace", versionProperty()
      )
      .buildAndFail()
    assertThat(result.output).contains("BUILD FAILED")
    assertThat(result.output).contains("Example.kt: (9, 3): @Exhaustive when is not exhaustive")
  }

  @Test fun jvmTest() {
    val fixtureDir = File(fixturesDir, "jvm-test")
    val gradleRoot = File(fixtureDir, "gradle").also { it.mkdir() }
    File("../gradle/wrapper").copyRecursively(File(gradleRoot, "wrapper"), true)

    val result = GradleRunner.create()
      .withProjectDir(fixtureDir)
      .withArguments(
        "clean", "compileTestKotlin", "--stacktrace", versionProperty()
      )
      .buildAndFail()
    assertThat(result.output).contains("BUILD FAILED")
    assertThat(result.output).contains("ExampleTest.kt: (9, 3): @Exhaustive when is not exhaustive")
  }
}
