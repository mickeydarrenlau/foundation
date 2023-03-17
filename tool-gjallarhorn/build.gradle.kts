plugins {
  id("gay.pizza.foundation.concrete-base")
  id("com.github.johnrengelman.shadow")
}

dependencies {
  implementation(project(":common-heimdall"))

  implementation(libs.clikt)
  implementation(libs.slf4j.simple)
}

tasks.jar {
  manifest.attributes(
    "Main-Class" to "gay.pizza.foundation.heimdall.tool.MainKt"
  )
}

tasks.assemble {
  dependsOn("shadowJar")
}

concreteItem {
  type.set("tool")

  fileInclusion {
    tasks.shadowJar.get().outputs.files.associateWith { "tool-jar" }
  }
}
