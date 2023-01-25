package gay.pizza.foundation.gjallarhorn.render

import gay.pizza.foundation.gjallarhorn.state.BlockStateMap
import gay.pizza.foundation.gjallarhorn.state.ChangelogSlice

interface BlockMapRenderer<T> {
  fun render(slice: ChangelogSlice, map: BlockStateMap): T
}
