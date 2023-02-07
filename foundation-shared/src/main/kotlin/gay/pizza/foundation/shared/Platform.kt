package gay.pizza.foundation.shared

object Platform {
  private val os: String? = System.getProperty("os.name")

  fun isWindows(): Boolean = os != null && os.lowercase().startsWith("windows")
}
