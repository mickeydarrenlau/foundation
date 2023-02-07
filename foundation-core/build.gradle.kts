plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  implementation(project(":foundation-shared"))

  implementation("software.amazon.awssdk:s3:2.19.31")
  implementation("org.quartz-scheduler:quartz:2.3.2")
  implementation("com.google.guava:guava:31.1-jre")
}
