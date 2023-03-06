package gay.pizza.foundation.heimdall.load

import gay.pizza.foundation.heimdall.export.ExportedBlock

class ExportedBlockTable {
  private val internalBlocks = mutableListOf<ExportedBlock>()

  val blocks: List<ExportedBlock>
    get() = internalBlocks

  fun index(block: ExportedBlock): Int {
    val existing = internalBlocks.indexOf(block)
    if (existing >= 0) {
      return existing
    }
    internalBlocks.add(block)
    return internalBlocks.size - 1
  }
}
