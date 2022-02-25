package cloud.kubelet.foundation.gjallarhorn.util

import org.jetbrains.exposed.sql.Op

fun compose(
  combine: (Op<Boolean>, Op<Boolean>) -> Op<Boolean>,
  vararg filters: Pair<() -> Boolean, () -> Op<Boolean>>
): Op<Boolean> = filters.toMap().entries
  .asSequence()
  .filter { it.key() }
  .map { it.value() }
  .fold(Op.TRUE as Op<Boolean>, combine)
