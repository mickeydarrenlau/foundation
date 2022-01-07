dependencies {
  implementation(project(":foundation-core"))
  implementation(project(":foundation-heimdall"))
  implementation("org.slf4j:slf4j-simple:1.7.32")
  implementation("com.github.ajalt.clikt:clikt:3.3.0")
}

listOf(tasks.jar, tasks.shadowJar).map { it.get() }.forEach { task ->
  task.manifest.attributes["Main-Class"] = "cloud.kubelet.foundation.gjallarhorn.MainKt"
}
