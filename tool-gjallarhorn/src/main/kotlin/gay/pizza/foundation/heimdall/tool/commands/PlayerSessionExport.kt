package gay.pizza.foundation.heimdall.tool.commands

import gay.pizza.foundation.heimdall.tool.util.compose
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import gay.pizza.foundation.heimdall.table.PlayerSessionTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class PlayerSessionExport : CliktCommand(name = "export-player-sessions", help = "Export Player Sessions") {
  private val db by requireObject<Database>()

  private val playerIdString by option("--player-id", help = "Player ID")
  private val playerNameString by option("--player-name", help = "Player Name")

  override fun run() {
    val filter = compose(
      combine = { a, b -> a and b },
      { playerIdString != null } to { gay.pizza.foundation.heimdall.table.PlayerSessionTable.player eq UUID.fromString(playerIdString) },
      { playerNameString != null } to { gay.pizza.foundation.heimdall.table.PlayerSessionTable.name eq playerNameString!! }
    )

    println("id,player,name,start,end")
    transaction(db) {
      gay.pizza.foundation.heimdall.table.PlayerSessionTable.select(filter).orderBy(gay.pizza.foundation.heimdall.table.PlayerSessionTable.endTime).forEach { row ->
        val id = row[gay.pizza.foundation.heimdall.table.PlayerSessionTable.id]
        val player = row[gay.pizza.foundation.heimdall.table.PlayerSessionTable.player]
        val name = row[gay.pizza.foundation.heimdall.table.PlayerSessionTable.name]
        val start = row[gay.pizza.foundation.heimdall.table.PlayerSessionTable.startTime]
        val end = row[gay.pizza.foundation.heimdall.table.PlayerSessionTable.endTime]

        println("${id},${player},${name},${start},${end}")
      }
    }
  }
}
