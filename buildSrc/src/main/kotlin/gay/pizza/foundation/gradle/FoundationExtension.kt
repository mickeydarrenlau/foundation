package gay.pizza.foundation.gradle

import org.gradle.api.provider.Property

interface FoundationExtension {
  val paperVersionGroup: Property<String>
  val minecraftServerPath: Property<String>
}
