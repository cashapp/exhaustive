package app.cash.exhaustive.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class ExhaustivePlugin : KotlinCompilerPluginSupportPlugin {
  override fun getCompilerPluginId() = "app.cash.exhaustive"

  override fun getPluginArtifact() = SubpluginArtifact(
    "app.cash.exhaustive",
    "exhaustive-compiler",
    exhaustiveVersion
  )

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
    return kotlinCompilation.target.project.plugins.hasPlugin(ExhaustivePlugin::class.java)
  }

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    kotlinCompilation.dependencies {
      compileOnly("app.cash.exhaustive:exhaustive-annotation:$exhaustiveVersion")
    }
    return kotlinCompilation.target.project.provider { emptyList() }
  }
}
