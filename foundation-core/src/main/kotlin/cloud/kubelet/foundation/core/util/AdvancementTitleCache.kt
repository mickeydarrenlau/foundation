package cloud.kubelet.foundation.core.util

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.advancement.Advancement
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

private fun Advancement.getInternalHandle(): Any =
  javaClass.getMethod("getHandle").invoke(this)

private fun Class<*>.getDeclaredFieldAccessible(name: String): Field {
  val field = getDeclaredField(name)
  if (!field.trySetAccessible()) {
    throw RuntimeException("Failed to set reflection permissions to accessible.")
  }
  return field
}

private fun Advancement.getInternalAdvancementDisplay(handle: Any = getInternalHandle()): Any? =
  handle.javaClass.methods.firstOrNull {
    it.returnType.simpleName == "AdvancementDisplay" &&
        it.parameterCount == 0
  }?.invoke(handle) ?: handle.javaClass.getDeclaredFieldAccessible("c").get(handle)

private fun Advancement.displayTitleText(): String? {
  val handle = getInternalHandle()
  val advancementDisplay = getInternalAdvancementDisplay(handle) ?: return null
  try {
    val field = advancementDisplay.javaClass.getDeclaredField("a")
    field.trySetAccessible()
    val message = field.get(advancementDisplay)
    val title = message.javaClass.getMethod("getString").invoke(message)
    return title.toString()
  } catch (_: Exception) {
  }

  val titleComponentField = advancementDisplay.javaClass.declaredFields.firstOrNull {
    it.type.simpleName == "IChatBaseComponent"
  }

  if (titleComponentField != null) {
    titleComponentField.trySetAccessible()
    val titleChatBaseComponent = titleComponentField.get(advancementDisplay)
    val title = titleChatBaseComponent.javaClass.getMethod("getText").invoke(titleChatBaseComponent).toString()
    if (title.isNotBlank()) {
      return title
    }

    val chatSerializerClass = titleChatBaseComponent.javaClass.declaredClasses.firstOrNull {
      it.simpleName == "ChatSerializer"
    }

    if (chatSerializerClass != null) {
      val componentJson = chatSerializerClass
          .getMethod("a", titleChatBaseComponent.javaClass)
          .invoke(null, titleChatBaseComponent).toString()
      val gson = GsonComponentSerializer.gson().deserialize(componentJson)
      return LegacyComponentSerializer.legacySection().serialize(gson)
    }
  }

  val rawAdvancementName = key.key
  return rawAdvancementName.substring(rawAdvancementName.lastIndexOf("/") + 1)
    .lowercase().split("_")
    .joinToString(" ") { it.substring(0, 1).uppercase() + it.substring(1) }
}

object AdvancementTitleCache {
  private val cache = ConcurrentHashMap<Advancement, String?>()

  fun of(advancement: Advancement): String? =
    cache.computeIfAbsent(advancement) { it.displayTitleText() }
}
