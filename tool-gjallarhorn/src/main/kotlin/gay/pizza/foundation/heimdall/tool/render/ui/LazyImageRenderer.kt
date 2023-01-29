package gay.pizza.foundation.heimdall.tool.render.ui

import gay.pizza.foundation.heimdall.tool.render.BlockImageRenderer
import gay.pizza.foundation.heimdall.tool.state.BlockStateMap
import gay.pizza.foundation.heimdall.tool.state.ChangelogSlice
import java.awt.Graphics
import javax.swing.JComponent

class LazyImageRenderer(val map: BlockStateMap, private val renderer: BlockImageRenderer) : JComponent() {
  private val image by lazy {
    renderer.render(ChangelogSlice.none, map)
  }

  override fun paint(g: Graphics?) {
    g?.drawImage(image, 0, 0, this)
  }

  override fun paintComponent(g: Graphics?) {
    g?.drawImage(image, 0, 0, this)
  }
}
