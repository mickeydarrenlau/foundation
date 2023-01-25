package gay.pizza.foundation.core.features.persist

import jetbrains.exodus.entitystore.Entity

fun <T : Comparable<*>> Entity.setAllProperties(vararg entries: Pair<String, T>) = entries.forEach { entry ->
  setProperty(entry.first, entry.second)
}
