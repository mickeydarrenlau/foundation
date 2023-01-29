package gay.pizza.foundation.heimdall.tool.commands

import gay.pizza.foundation.heimdall.tool.render.*
import gay.pizza.foundation.heimdall.tool.state.BlockExpanse
import org.jetbrains.exposed.sql.Database

@Suppress("unused")
enum class ImageRenderType(
  val id: String,
  val createNewRenderer: (BlockExpanse, Database) -> BlockImageRenderer
) {
  BlockDiversity("block-diversity", { expanse, _ -> BlockDiversityRenderer(expanse) }),
  HeightMap("height-map", { expanse, _ -> BlockHeightMapRenderer(expanse) }),
  PlayerPosition("player-position", { expanse, db -> PlayerLocationShareRenderer(expanse, db) }),
  GraphicalSession("graphical", { expanse, _ -> LaunchGraphicalRenderSession(expanse) })
}
