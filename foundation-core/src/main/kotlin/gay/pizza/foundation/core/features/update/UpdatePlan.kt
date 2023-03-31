package gay.pizza.foundation.core.features.update

import gay.pizza.foundation.concrete.ExtensibleManifestItem
import org.bukkit.plugin.Plugin

class UpdatePlan(
  val installedSet: Map<ExtensibleManifestItem, Plugin?>,
  val updateSet: Map<ExtensibleManifestItem, Plugin?>
)
