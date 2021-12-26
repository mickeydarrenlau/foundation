package cloud.kubelet.foundation.gjallarhorn

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import org.jetbrains.exposed.sql.Database

class GjallarhornCommand : CliktCommand(invokeWithoutSubcommand = true) {
  private val jdbcConnectionUrl by option("-c", "--connection-url", help = "JDBC Connection URL")
    .default("jdbc:postgresql://localhost/foundation")

  private val jdbcConnectionUsername by option("-u", "--connection-username", help = "JDBC Connection Username")
    .default("jdbc:postgresql://localhost/foundation")

  private val jdbcConnectionPassword by option("-p", "--connection-password", help = "JDBC Connection Passowrd")
    .default("jdbc:postgresql://localhost/foundation")

  override fun run() {
    val db = Database.connect(jdbcConnectionUrl, user = jdbcConnectionUsername, password = jdbcConnectionPassword)
    currentContext.findOrSetObject { db }
  }
}
