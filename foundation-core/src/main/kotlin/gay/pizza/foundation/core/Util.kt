package gay.pizza.foundation.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

object Util {
  private val leftBracket: Component = Component.text('[')
  private val rightBracket: Component = Component.text(']')
  private val whitespace: Component = Component.text(' ')
  private val foundationName: Component = Component.text("Foundation")

  fun formatSystemMessage(message: String): Component {
    return formatSystemMessage(TextColors.AmaranthPink, message)
  }

  fun formatSystemMessage(prefixColor: TextColor, message: String): Component {
    return leftBracket
      .append(foundationName.color(prefixColor))
      .append(rightBracket)
      .append(whitespace)
      .append(Component.text(message))
  }
}
