plugins {
  id("gay.pizza.foundation.concrete-plugin")
}

dependencies {
  api(project(":common-all"))
  implementation(project(":foundation-shared"))

  implementation("software.amazon.awssdk:s3:2.19.31")
  implementation("org.quartz-scheduler:quartz:2.3.2")
  implementation("com.google.guava:guava:31.1-jre")

  implementation("io.insert-koin:koin-core:3.3.2")
  testImplementation("io.insert-koin:koin-test:3.3.2")
}
