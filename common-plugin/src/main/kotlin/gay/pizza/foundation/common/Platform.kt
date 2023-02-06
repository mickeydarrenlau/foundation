package gay.pizza.foundation.common

object Platform {
  private val os: String? = System.getProperty("os.name")

  fun isWindows(): Boolean = os != null && os.lowercase().startsWith("windows")
}
