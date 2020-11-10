package app.cash.exhaustive

import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * Denote a `when` statement which is required to exhaustively cover all cases.
 *
 * The Kotlin compiler will only enforce exhaustiveness for a `when` used as an expression. This
 * annotation extends that same enforcement to statement `when`s. Additionally, these `when`s are
 * forbidden from having an `else` branch since it is mutually exclusive with exhaustiveness.
 *
 * ```kotlin
 * enum class RouletteColor { Red, Black, Green }
 *
 * fun printColor(color: RouletteColor) {
 *   @Exhaustive
 *   when (subject) {
 *     Red -> println("red")
 *     Black -> println("black")
 *   }
 * }
 * ```
 * Compilation of the above will fail with:
 * ```
 * e: Example.kt:5: @Exhaustive when is not exhaustive!
 *
 * Missing branches:
 * - RouletteColor.Green
 * ```
 */
@Retention(SOURCE)
annotation class Exhaustive
