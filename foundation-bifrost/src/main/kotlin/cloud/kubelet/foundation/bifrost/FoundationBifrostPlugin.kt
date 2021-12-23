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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Color
import kotlin.io.path.inputStream

class FoundationBifrostPlugin : JavaPlugin(), EventListener, Listener {
  private lateinit var config: BifrostConfig
  private lateinit var jda: JDA

  override fun onEnable() {
    val foundation = server.pluginManager.getPlugin("Foundation") as FoundationCorePlugin
    slF4JLogger.info("Plugin data path: ${foundation.pluginDataPath}")

    val configPath = Util.copyDefaultConfig(slF4JLogger, foundation.pluginDataPath, "bifrost.yaml")
    config = Yaml.default.decodeFromStream(BifrostConfig.serializer(), configPath.inputStream())

    server.pluginManager.registerEvents(this, this)

    jda = JDABuilder
      .createDefault(config.authentication.token)
      .addEventListeners(this)
      .build()
  }

  override fun onEvent(e: GenericEvent) {
    when (e) {
      is MessageReceivedEvent -> {
        // Prevent this bot from receiving its own messages and creating a feedback loop.
        if (e.author.id == jda.selfUser.id) return

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
    val channel = getChannel() ?: return
    val message = e.message()

    if (message is TextComponent) {
      channel.sendMessage("${e.player.name}: ${message.content()}").queue()
    } else {
      slF4JLogger.error("Not sure what to do here, message != TextComponent: ${message.javaClass}")
    }
  }
}
