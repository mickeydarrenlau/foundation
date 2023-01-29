package gay.pizza.foundation.heimdall.tool.state

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
      PlayerPositionChangelog(gay.pizza.foundation.heimdall.table.PlayerPositionTable.select(filter).orderBy(gay.pizza.foundation.heimdall.table.PlayerPositionTable.time).map { row ->
        val time = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.time]
        val player = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.player]
        val world = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.world]
        val x = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.x]
        val y = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.y]
        val z = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.z]
        val pitch = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.z]
        val yaw = row[gay.pizza.foundation.heimdall.table.PlayerPositionTable.z]

        PlayerPositionChange(time, player, world, x, y, z, pitch, yaw)
      })
    }
  }
}
