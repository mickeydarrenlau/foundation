package gay.pizza.foundation.tailscale

import gay.pizza.tailscale.channel.ChannelCopier
import gay.pizza.tailscale.core.Tailscale
import gay.pizza.tailscale.core.TailscaleConn
import gay.pizza.tailscale.core.TailscaleListener
import org.bukkit.Server
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.channels.SocketChannel

class TailscaleProxyServer(val server: Server, val tailscale: Tailscale) {
  private var minecraftServerListener: TailscaleListener? = null

  fun listen() {
    minecraftServerListener?.close()
    minecraftServerListener = tailscale.listen("tcp", ":25565")
    val thread = Thread {
      minecraftServerListener?.threadedAcceptLoop { conn ->
        handleServerConnection(conn)
      }
    }
    thread.name = "Tailscale Accept Loop"
    thread.start()
  }

  fun handleServerConnection(conn: TailscaleConn) {
    val socketChannel = SocketChannel.open()
    socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true)
    socketChannel.connect(InetSocketAddress("127.0.0.1", server.port))
    val readChannel = conn.openReadChannel()
    val writeChannel = conn.openWriteChannel()

    val closeHandler = { socketChannel.close() }
    val tailscaleSocketCopier = ChannelCopier(readChannel, socketChannel)
    tailscaleSocketCopier.spawnCopyThread(
      "Tailscale to Socket Copier", onClose = closeHandler)
    val socketTailscaleCopier = ChannelCopier(socketChannel, writeChannel)
    socketTailscaleCopier.spawnCopyThread(
      "Socket to Tailscale Copier", onClose = closeHandler)
  }

  fun close() {
    minecraftServerListener?.close()
  }
}
