package cloud.kubelet.foundation.gjallarhorn.commands

import cloud.kubelet.foundation.gjallarhorn.util.compose
import cloud.kubelet.foundation.heimdall.table.PlayerPositionTable
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class PlayerPositionExport : CliktCommand(name = "export-player-positions", help = "Export Player Positions") {
  private val db by requireObject<Database>()

  private val playerIdString by option("--player", help = "Player ID")
  private val startTimeString by option("--start-time", help = "Start Time")
  private val endTimeString by option("--end-time", help = "End Time")

  override fun run() {
    val filter = compose(
      combine = { a, b -> a and b },
      { startTimeString != null } to { PlayerPositionTable.time greaterEq Instant.parse(startTimeString) },
      { endTimeString != null } to { PlayerPositionTable.time lessEq Instant.parse(endTimeString) },
      { playerIdString != null } to { PlayerPositionTable.player eq UUID.fromString(playerIdString) }
    )

    println("time,player,world,x,y,z,pitch,yaw")
    transaction(db) {
      PlayerPositionTable.select(filter).orderBy(PlayerPositionTable.time).forEach { row ->
        val time = row[PlayerPositionTable.time]
        val player = row[PlayerPositionTable.player]
        val world = row[PlayerPositionTable.world]
        val x = row[PlayerPositionTable.x]
        val y = row[PlayerPositionTable.y]
        val z = row[PlayerPositionTable.z]
        val pitch = row[PlayerPositionTable.pitch]
        val yaw = row[PlayerPositionTable.yaw]

        println("${time},${player},${world},${x},${y},${z},${pitch},${yaw}")
      }
    }
  }
}
