package cloud.kubelet.foundation.gjallarhorn.render.ui

import cloud.kubelet.foundation.gjallarhorn.render.BlockDiversityRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockHeightMapRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockVerticalFillMapRenderer
import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.state.BlockStateMap
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JTabbedPane

class GraphicalRenderSession(val expanse: BlockExpanse, val map: BlockStateMap) : JFrame() {
  init {
    name = "Gjallarhorn Renderer"
    size = Dimension(1024, 1024)
    val pane = JTabbedPane()
    pane.addTab("Block Diversity", LazyImageRenderer(map, BlockDiversityRenderer(expanse)))
    pane.addTab("Height Map", LazyImageRenderer(map, BlockHeightMapRenderer(expanse)))
    pane.addTab("Vertical Fill Map", LazyImageRenderer(map, BlockVerticalFillMapRenderer(expanse)))
    add(pane)
  }
}
