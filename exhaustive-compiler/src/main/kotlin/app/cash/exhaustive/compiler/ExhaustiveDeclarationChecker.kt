package app.cash.exhaustive.compiler

import app.cash.exhaustive.compiler.ExhaustiveErrors.INVALID_ELSE_BRANCH
import app.cash.exhaustive.compiler.ExhaustiveErrors.NOT_EXHAUSTIVE
import app.cash.exhaustive.compiler.ExhaustiveErrors.WHEN_SUBJECT_REQUIRED
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.BindingContext.ANNOTATION
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

internal object ExhaustiveDeclarationChecker : DeclarationChecker {
  private val exhaustiveAnnotation = FqName("app.cash.exhaustive.Exhaustive")

  override fun check(
    declaration: KtDeclaration,
    descriptor: DeclarationDescriptor,
    context: DeclarationCheckerContext,
  ) {
    val bindingContext = context.trace.bindingContext

    declaration.accept(object : KtTreeVisitorVoid() {
      override fun visitAnnotatedExpression(expression: KtAnnotatedExpression) {
        val whenExpression = expression.baseExpression
        if (whenExpression is KtWhenExpression) {
          val wantsExhaustive = expression.annotationEntries
            .any { exhaustiveAnnotation == bindingContext.get(ANNOTATION, it)?.fqName }
          if (wantsExhaustive) {
            validateExhaustiveWhen(whenExpression, context.trace)
          }
        }

        super.visitAnnotatedExpression(expression)
      }
    })
  }

  private fun validateExhaustiveWhen(
    whenExpression: KtWhenExpression,
    trace: BindingTrace,
  ) {
    whenExpression.elseExpression?.let { elseExpression ->
      trace.report(INVALID_ELSE_BRANCH.on(elseExpression))
      return
    }

    if (whenExpression.subjectExpression == null) {
      trace.report(WHEN_SUBJECT_REQUIRED.on(whenExpression))
      return
    }

    val missingCases = WhenChecker.getMissingCases(whenExpression, trace.bindingContext)
    if (missingCases.isNotEmpty()) {
      trace.report(NOT_EXHAUSTIVE.on(whenExpression, missingCases))
    }
  }
}
