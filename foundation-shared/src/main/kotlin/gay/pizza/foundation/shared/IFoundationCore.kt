package gay.pizza.foundation.shared

import java.nio.file.Path

interface IFoundationCore {
  val persistence: PluginPersistence
  val pluginDataPath: Path
}
