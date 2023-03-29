package gay.pizza.foundation.tailscale

import gay.pizza.tailscale.core.Tailscale
import gay.pizza.tailscale.core.TailscaleConn
import gay.pizza.tailscale.core.TailscaleListener
import org.bukkit.Server
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SocketChannel
import java.nio.channels.WritableByteChannel

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

    fun closeAll() {
      socketChannel.close()
      readChannel.close()
      writeChannel.close()
    }

    fun startCopyThread(name: String, from: ReadableByteChannel, to: WritableByteChannel) {
      val thread = Thread {
        try {
          while (true) {
            val buffer = ByteBuffer.allocate(2048)
            val size = from.read(buffer)
            if (size < 0) {
              break
            } else {
              buffer.flip()
              to.write(buffer)
            }
            buffer.clear()
          }
        } catch (_: ClosedChannelException) {
        } finally {
          closeAll()
        }
      }

      thread.name = name
      thread.start()
    }

    startCopyThread("Tailscale to Socket Pipe", readChannel, socketChannel)
    startCopyThread("Socket to Tailscale Pipe", socketChannel, writeChannel)
  }

  fun close() {
    minecraftServerListener?.close()
  }
}
