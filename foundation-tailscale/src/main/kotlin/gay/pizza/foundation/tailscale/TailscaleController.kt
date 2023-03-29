package gay.pizza.foundation.tailscale

import gay.pizza.tailscale.core.Tailscale
import org.bukkit.Server

class TailscaleController(val server: Server, val config: TailscaleConfig) {
  private val tailscale = Tailscale()

  var tailscaleProxyServer: TailscaleProxyServer? = null

  fun enable() {
    if (!config.enabled) {
      return
    }
    tailscale.useProcSelfFd = config.useProcSelfFd
    tailscale.hostname = config.hostname

    if (config.controlUrl != null) {
      tailscale.controlUrl = config.controlUrl
    }

    if (config.authKey != null) {
      tailscale.authKey = config.authKey
    }

    if (config.tailscalePath != null) {
      tailscale.directoryPath = config.tailscalePath
    }

    tailscale.up()
    tailscaleProxyServer = TailscaleProxyServer(server, tailscale)
    tailscaleProxyServer?.listen()
  }

  fun disable() {
    tailscaleProxyServer?.close()
    tailscale.close()
  }
}
