package cloud.kubelet.foundation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.slf4j.Logger

object Util {
  private val leftBracket: Component = Component.text('[')
  private val rightBracket: Component = Component.text(']')
  private val whitespace: Component = Component.text(' ')
  private val foundationName: Component = Component.text("Foundation")

  fun printFeatureStatus(logger: Logger, feature: String?, state: Boolean) {
    logger.info("{}: {}", feature, if (state) "Enabled" else "Disabled")
  }

  fun formatSystemMessage(message: String?): Component {
    return formatSystemMessage(TextColors.AMARANTH_PINK, message)
  }

  fun formatSystemMessage(prefixColor: TextColor?, message: String?): Component {
    return leftBracket
      .append(foundationName.color(prefixColor))
      .append(rightBracket)
      .append(whitespace)
      .append(Component.text(message!!))
  }
}