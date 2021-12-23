package cloud.kubelet.foundation.bifrost

import cloud.kubelet.foundation.bifrost.model.BifrostConfig
import cloud.kubelet.foundation.core.FoundationCorePlugin
import cloud.kubelet.foundation.core.Util
import com.charleskorn.kaml.Yaml
import io.papermc.paper.event.player.AsyncChatEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Color
import kotlin.io.path.inputStream

class FoundationBifrostPlugin : JavaPlugin(), EventListener, Listener {
  private lateinit var config: BifrostConfig
  private lateinit var jda: JDA
  private var isDev = false

  override fun onEnable() {
    isDev = description.version == "DEV"

    val foundation = server.pluginManager.getPlugin("Foundation") as FoundationCorePlugin
    slF4JLogger.info("Plugin data path: ${foundation.pluginDataPath}")

    val configPath = Util.copyDefaultConfig<FoundationBifrostPlugin>(
      slF4JLogger,
      foundation.pluginDataPath,
      "bifrost.yaml"
    )
    config = Yaml.default.decodeFromStream(BifrostConfig.serializer(), configPath.inputStream())

    if (config.authentication.token.isEmpty()) {
      slF4JLogger.warn("Token empty, will not start Bifrost.")
      return
    }

    server.pluginManager.registerEvents(this, this)

    jda = JDABuilder
      .createDefault(config.authentication.token)
      .addEventListeners(this)
      .build()
  }

  override fun onDisable() {
    // Plugin was not initialized, don't do anything.
    if (!::jda.isInitialized) return

    onServerStop()

    logger.info("Shutting down JDA")
    jda.shutdown()
    while (jda.status != JDA.Status.SHUTDOWN) {
      Thread.sleep(100)
    }
  }

  override fun onEvent(e: GenericEvent) {
    when (e) {
      is ReadyEvent -> {
        onDiscordReady()
      }
      is MessageReceivedEvent -> {
        if (!config.channel.bridge) return
        // Prevent this bot from receiving its own messages and creating a feedback loop.
        if (e.author.id == jda.selfUser.id) return

        // Only forward messages from the configured channel.
        if (e.channel.id != config.channel.id) return

        slF4JLogger.debug(
          "${e.guild.name} - ${e.channel.name} - ${e.author.name}: ${e.message.contentDisplay}"
        )
        server.sendMessage(Component.text("${e.author.name} - ${e.message.contentDisplay}"))
      }
    }
  }

  private fun getChannel(): TextChannel? {
    val channel = jda.getTextChannelById(config.channel.id)
    if (channel == null) {
      slF4JLogger.error("Failed to retrieve channel ${config.channel.id}")
    }
    return channel
  }

  private fun message(f: MessageBuilder.() -> Unit) = MessageBuilder().apply(f).build()
  private fun MessageBuilder.embed(f: EmbedBuilder.() -> Unit) {
    setEmbeds(EmbedBuilder().apply(f).build())
  }

  @EventHandler
  private fun onPlayerJoin(e: PlayerJoinEvent) {
    if (!config.channel.sendPlayerJoin) return
    val channel = getChannel() ?: return

    channel.sendMessage(message {
      embed {
        setAuthor("${e.player.name} joined the server")
        setColor(Color.GREEN)
      }
    }).queue()
  }

  @EventHandler
  private fun onPlayerQuit(e: PlayerQuitEvent) {
    if (!config.channel.sendPlayerQuit) return
    val channel = getChannel() ?: return

    channel.sendMessage(message {
      embed {
        setAuthor("${e.player.name} left the server")
        setColor(Color.RED)
      }
    }).queue()
  }

  @EventHandler
  private fun onPlayerChat(e: AsyncChatEvent) {
    if (!config.channel.bridge) return
    val channel = getChannel() ?: return
    val message = e.message()

    if (message is TextComponent) {
      channel.sendMessage("${e.player.name}: ${message.content()}").queue()
    } else {
      slF4JLogger.error("Not sure what to do here, message != TextComponent: ${message.javaClass}")
    }
  }

  private fun onDiscordReady() {
    if (!config.channel.sendStart) return
    val channel = getChannel() ?: return
    if (isDev) return
    channel.sendMessage(":white_check_mark: Server is ready!").queue()
  }

  private fun onServerStop() {
    if (!config.channel.sendShutdown) return
    val channel = getChannel() ?: return
    if (isDev) return
    channel.sendMessage(":octagonal_sign: Server is stopping!").queue()
  }
}
