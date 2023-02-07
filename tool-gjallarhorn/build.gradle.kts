plugins {
  id("gay.pizza.foundation.concrete-base")
  id("com.github.johnrengelman.shadow")
}

dependencies {
  implementation(project(":common-heimdall"))

  implementation("com.github.ajalt.clikt:clikt:3.5.1")
  implementation("org.slf4j:slf4j-simple:2.0.6")
}

tasks.jar {
  manifest.attributes(
    "Main-Class" to "gay.pizza.foundation.heimdall.tool.MainKt"
  )
}

tasks.assemble {
  dependsOn("shadowJar")
}
