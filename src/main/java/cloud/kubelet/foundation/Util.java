package cloud.kubelet.foundation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.slf4j.Logger;

public class Util {

  private static final Component leftBracket = Component.text('[');
  private static final Component rightBracket = Component.text(']');
  private static final Component whitespace = Component.text(' ');
  private static final Component foundationName = Component.text("Foundation");

  public static void printFeatureStatus(Logger logger, String feature, boolean state) {
    logger.info("{}: {}", feature, state ? "Enabled" : "Disabled");
  }

  public static Component formatSystemMessage(String message) {
    return formatSystemMessage(TextColors.AMARANTH_PINK, message);
  }

  public static Component formatSystemMessage(TextColor prefixColor, String message) {
    return leftBracket
        .append(foundationName.color(prefixColor))
        .append(rightBracket)
        .append(whitespace)
        .append(Component.text(message));
  }
}
