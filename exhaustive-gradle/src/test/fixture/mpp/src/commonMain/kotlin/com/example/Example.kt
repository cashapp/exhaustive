package com.example

import app.cash.exhaustive.Exhaustive

enum class RouletteColor { Red, Black, Green }

fun subject(value: RouletteColor) {
  @Exhaustive
  when (value) {
    RouletteColor.Red -> println("red")
    RouletteColor.Black -> println("black")
  }
}
