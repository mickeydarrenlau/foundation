plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  // TODO: might be able to ship all dependencies in core? are we duplicating classes in JARs?
  implementation("software.amazon.awssdk:s3:2.17.102")
  implementation("org.quartz-scheduler:quartz:2.3.2")
  implementation("com.google.guava:guava:31.0.1-jre")

  api(project(":common-plugin"))
}
