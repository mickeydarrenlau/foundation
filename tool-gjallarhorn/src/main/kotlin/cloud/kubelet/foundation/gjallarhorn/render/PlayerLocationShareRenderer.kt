package cloud.kubelet.foundation.gjallarhorn.render

import cloud.kubelet.foundation.gjallarhorn.state.BlockCoordinate
import cloud.kubelet.foundation.gjallarhorn.state.BlockExpanse
import cloud.kubelet.foundation.gjallarhorn.state.BlockMap
import cloud.kubelet.foundation.gjallarhorn.state.ChangelogSlice
import cloud.kubelet.foundation.gjallarhorn.util.BlockColorKey
import cloud.kubelet.foundation.heimdall.table.PlayerPositionTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.awt.image.BufferedImage

class PlayerLocationShareRenderer(
  val expanse: BlockExpanse,
  val db: Database,
  quadPixelSize: Int = defaultQuadPixelSize) : BlockGridRenderer(quadPixelSize) {
  private val colorKey = BlockColorKey(mapOf())

  override fun render(slice: ChangelogSlice, map: BlockMap): BufferedImage {
    val start = slice.relativeChangeRange.start
    val end = slice.relativeChangeRange.endInclusive

    val playersToUniquePositions = transaction(db) {
      PlayerPositionTable.select {
        (PlayerPositionTable.time greater start) and
            (PlayerPositionTable.time lessEq end)
      }.map {
        val x = it[PlayerPositionTable.x].toLong()
        val y = it[PlayerPositionTable.y].toLong()
        val z = it[PlayerPositionTable.z].toLong()
        val coordinate = expanse.offset.applyAsOffset(BlockCoordinate(x, y, z))
        it[PlayerPositionTable.player] to coordinate
      }.distinct()
    }

    val colorOfPlayers = playersToUniquePositions.map { it.first }
      .distinct()
      .associateWith { colorKey.map(it.toString()) }

    return buildPixelQuadImage(expanse) { g, x, z ->
      val players = playersToUniquePositions.filter { it.second.x == x && it.second.z == z }.map { it.first }.distinct()
      if (players.isNotEmpty()) {
        setPixelQuad(g, x, z, colorOfPlayers[players.first()]!!)
      } else {
        setPixelQuad(g, x, z, Color.white)
      }
    }
  }
}
