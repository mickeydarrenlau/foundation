package cloud.kubelet.foundation.heimdall

import org.bukkit.Material

fun String.sqlSplitStatements(): List<String> {
  val statements = mutableListOf<String>()
  val buffer = StringBuilder()
  fun flush() {
    val trimmed = buffer.toString().trim()
    if (trimmed.isNotEmpty()) {
      statements.add(trimmed)
    }
  }
  for (line in lines()) {
    if (line.trim() == "--") {
      flush()
    } else {
      buffer.append(line).append("\n")
    }
  }
  flush()
  return statements
}

val Material.storageBlockName: String
  get() = "${key.namespace}:${key.key}"
