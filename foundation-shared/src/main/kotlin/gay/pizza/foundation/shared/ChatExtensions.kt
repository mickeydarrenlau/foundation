package gay.pizza.foundation.shared

import org.bukkit.entity.Player

fun Player.chat(vararg messages: String): Unit = messages.forEach { message ->
  chat(message)
}
