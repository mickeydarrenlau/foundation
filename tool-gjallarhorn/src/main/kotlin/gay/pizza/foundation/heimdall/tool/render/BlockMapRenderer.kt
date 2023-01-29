package gay.pizza.foundation.heimdall.tool.render

import gay.pizza.foundation.heimdall.tool.state.BlockStateMap
import gay.pizza.foundation.heimdall.tool.state.ChangelogSlice

interface BlockMapRenderer<T> {
  fun render(slice: ChangelogSlice, map: BlockStateMap): T
}
