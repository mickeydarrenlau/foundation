rootProject.name = "foundation"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://gitlab.com/api/v4/projects/42873094/packages/maven")
  }
}

include(
  ":foundation-core",
  ":foundation-bifrost",
  ":foundation-chaos",
)
