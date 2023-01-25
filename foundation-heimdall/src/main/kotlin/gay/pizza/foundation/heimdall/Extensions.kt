package gay.pizza.foundation.heimdall

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
