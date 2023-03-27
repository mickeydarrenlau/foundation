plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

repositories {
  maven {
    name = "GitLabLibtailscale"
    url = uri("https://gitlab.com/api/v4/projects/44435887/packages/maven")
  }
}

dependencies {
  implementation(project(":common-plugin"))
  compileOnly(project(":foundation-shared"))
  implementation(libs.tailscale)
  implementation(libs.tailscale.channel)
}

concreteItem {
  dependency(project(":foundation-core"))
}
