package gay.pizza.foundation.gjallarhorn.state

import gay.pizza.foundation.heimdall.table.PlayerPositionTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class PlayerPositionChangelog(
  val changes: List<PlayerPositionChange>
) {
  companion object {
    fun query(db: Database, filter: Op<Boolean> = Op.TRUE): PlayerPositionChangelog = transaction(db) {
      PlayerPositionChangelog(PlayerPositionTable.select(filter).orderBy(PlayerPositionTable.time).map { row ->
        val time = row[PlayerPositionTable.time]
        val player = row[PlayerPositionTable.player]
        val world = row[PlayerPositionTable.world]
        val x = row[PlayerPositionTable.x]
        val y = row[PlayerPositionTable.y]
        val z = row[PlayerPositionTable.z]
        val pitch = row[PlayerPositionTable.z]
        val yaw = row[PlayerPositionTable.z]

        PlayerPositionChange(time, player, world, x, y, z, pitch, yaw)
      })
    }
  }
}
