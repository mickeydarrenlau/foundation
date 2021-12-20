package cloud.kubelet.foundation;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;

public class Util {
  public static int runProcess(List<String> command) throws IOException, InterruptedException {
    return new ProcessBuilder(command).start().waitFor();
  }

  public static void printFeatureStatus(Logger logger, String feature, boolean state) {
    logger.info("{}: {}", feature, state ? "Enabled" : "Disabled");
  }
}
