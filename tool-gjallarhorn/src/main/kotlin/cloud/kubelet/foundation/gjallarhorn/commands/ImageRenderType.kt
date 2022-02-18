package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.render.BlockDiversityRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockHeightMapRenderer
import cloud.kubelet.foundation.gjallarhorn.render.BlockImageRenderer
import cloud.kubelet.foundation.gjallarhorn.render.PlayerLocationShareRenderer
import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import org.jetbrains.exposed.sql.Database

@Suppress("unused")
enum class ImageRenderType(
  val id: String,
  val create: (BlockExpanse, Database) -> BlockImageRenderer
) {
  BlockDiversity("block-diversity", { expanse, _ -> BlockDiversityRenderer(expanse) }),
  HeightMap("height-map", { expanse, _ -> BlockHeightMapRenderer(expanse) }),
  PlayerPosition("player-position", { expanse, db -> PlayerLocationShareRenderer(expanse, db) })
}
