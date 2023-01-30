rootProject.name = "foundation"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://gitlab.com/api/v4/projects/42873094/packages/maven")
  }
}

include(
  ":common-plugin",
  ":common-heimdall",
  ":foundation-core",
  ":foundation-bifrost",
  ":foundation-chaos",
  ":foundation-heimdall",
  ":tool-gjallarhorn",
)
