package gay.pizza.foundation.heimdall.tool.render

import gay.pizza.foundation.heimdall.tool.render.ui.GraphicalRenderSession
import gay.pizza.foundation.heimdall.tool.state.BlockExpanse
import gay.pizza.foundation.heimdall.tool.state.BlockStateMap
import gay.pizza.foundation.heimdall.tool.state.ChangelogSlice
import java.awt.image.BufferedImage
import javax.swing.WindowConstants

class LaunchGraphicalRenderSession(val expanse: BlockExpanse) : BlockImageRenderer {
  override fun render(slice: ChangelogSlice, map: BlockStateMap): BufferedImage {
    val session = GraphicalRenderSession(expanse, map)
    session.isVisible = true
    session.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
    while (session.isVisible) {
      Thread.sleep(1000)
    }
    return BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)
  }
}
