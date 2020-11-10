package app.cash.exhaustive.compiler

import app.cash.exhaustive.compiler.ExhaustiveErrors.INVALID_ELSE_BRANCH
import app.cash.exhaustive.compiler.ExhaustiveErrors.NOT_EXHAUSTIVE
import app.cash.exhaustive.compiler.ExhaustiveErrors.WHEN_SUBJECT_REQUIRED
import org.jetbrains.kotlin.cfg.WhenMissingCase
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.Severity.ERROR
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.MultiRenderer

internal object ExhaustiveErrors {
  @JvmField
  val INVALID_ELSE_BRANCH = DiagnosticFactory0.create<PsiElement>(ERROR)
  @JvmField
  val NOT_EXHAUSTIVE = DiagnosticFactory1.create<PsiElement, List<WhenMissingCase>>(ERROR)
  @JvmField
  val WHEN_SUBJECT_REQUIRED = DiagnosticFactory0.create<PsiElement>(ERROR)

  init {
    Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(
      ExhaustiveErrors::class.java,
      DefaultErrorMessagesExhaustive,
    )
  }
}

private object DefaultErrorMessagesExhaustive : DefaultErrorMessages.Extension {
  private val map = DiagnosticFactoryToRendererMap("Exhaustive").apply {
    put(INVALID_ELSE_BRANCH, "@Exhaustive when must not contain an 'else' branch")
    put(WHEN_SUBJECT_REQUIRED, "@Exhaustive when must have a subject expression")

    put(
      NOT_EXHAUSTIVE,
      "@Exhaustive when is not exhaustive!\n\nMissing branches:\n{0}",
      object : MultiRenderer<List<WhenMissingCase>> {
        override fun render(a: List<WhenMissingCase>): Array<String> {
          return arrayOf(
            a.joinToString(prefix = "- ", separator = "\n- ") { it.branchConditionText },
          )
        }
      },
    )
  }

  override fun getMap() = map
}
